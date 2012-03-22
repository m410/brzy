package org.brzy.mod.devmode

import java.io.{File}
import java.net.{URL, URLClassLoader}
import javax.servlet.{ServletResponse, ServletRequest, ServletConfig}
import org.brzy.application.WebApp
import tools.nsc.reporters.ConsoleReporter
import tools.nsc.{Global, Settings}
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import org.brzy.webapp.action.args.{Principal, Arg, PrincipalRequest, ArgsBuilder}
import org.slf4j.LoggerFactory
import org.brzy.webapp.action.response._
import actors.Futures._
import actors.Future

/**
 * Accepts requests to the application like the Brzy servlet, but checks for changes in
 * the source and recompiles and reloads the application.
 *
 * @author Michael Fortin
 */
class BrzyAppServlet extends HttpServlet {
  private[this] val log = LoggerFactory.getLogger(classOf[BrzyAppServlet])
  private[this] var applicationLoader: URLClassLoader = _
  private[this] var webApp: WebApp = _
  private[this] var lastModified = System.currentTimeMillis() - 1000 // need to round to the previous second
  private[this] var classpath:String = _
  private[this] var sourceDir:File = _
  private[this] var classesDir:File = _

  var compiling:Future[String] = new Future[String] {
    def isSet = true
    def apply() = null
    def respond(k: (String) => Unit) {}
    def inputChannel = null
  }

  override def init(config: ServletConfig) {
    sourceDir = new File(config.getInitParameter("source_dir"))
    classesDir = new File(config.getInitParameter("classes_dir"))
    classpath = config.getInitParameter("classpath")
    log.info("source_dir: '{}'", sourceDir)
    log.info("classes_dir: '{}'", classesDir)
    log.info("classpath: '{}'", classpath)
    log.info("last modified: '{}'", lastModified)
    webApp = makeApplication()
    config.getServletContext.setAttribute("application", webApp)
  }

  override def service(req: ServletRequest, res: ServletResponse) {
    internal(req.asInstanceOf[HttpServletRequest], res.asInstanceOf[HttpServletResponse])
  }

  private[this] def sourceModified: Option[List[File]] = {
    val files = findFiles(sourceDir).filter(f=>{
      f.lastModified() > lastModified
    })
    
    if (files.isEmpty) {
      None
    }
    else {
      Option(files)
    }
  }

  private[this] def makeApplication() = {
    val cp = classpath.split(":").map(f=>{new File(f).toURI.toURL})
    applicationLoader = new URLClassLoader(cp, getClass.getClassLoader)
    val clazz = applicationLoader.loadClass("org.brzy.test.ApplicationLoader")
    val declaredConstructor = clazz.getDeclaredConstructor(Array.empty[Class[_]]: _*)
    declaredConstructor.setAccessible(true)
    val inst = declaredConstructor.newInstance()
    val method = clazz.getMethod("load", Array.empty[Class[_]]: _*)
    val a = method.invoke(inst)
    a.getClass.getMethod("startup").invoke(a)
    a.asInstanceOf[WebApp]
  }

  private[this] def recompileSource(files: List[File]) {
    def error(s: String) {
      println(s)
    }
    val settings = new Settings(error)
    settings.classpath.value = classpath
    settings.sourcedir.value = sourceDir.getAbsolutePath
    settings.outdir.value = classesDir.getAbsolutePath
    settings.deprecation.value = true // enable detailed deprecation warnings
    settings.unchecked.value = true // enable detailed unchecked warnings

    val reporter = new ConsoleReporter(settings)
    val compiler = new Global(settings, reporter)
    (new compiler.Run).compile(files.map(_.getAbsolutePath))

    reporter.printSummary()
    if (reporter.hasErrors || reporter.WARNING.count > 0) {
      //      ...
    }
  }

  private[this] def stopApplication() {
    webApp.shutdown()
    webApp = null
  }

  private[this] def internal(req: HttpServletRequest, res: HttpServletResponse) {
    log.trace("request: {}, context: {}", req.getServletPath, req.getContextPath)
    val actionPath = ArgsBuilder.parseActionPath(req.getRequestURI, req.getContextPath)
    log.trace("action-path: {}", actionPath)

    if (!compiling.isSet) {
      log.warn("Still Compiling Source...")
      res.getOutputStream.write("Waiting".getBytes("UTF-8"))
    }
    else {
      sourceModified  match {
        case Some(files) =>
          compiling = future {
            lastModified = System.currentTimeMillis() - 1000
            log.warn("Recompiling Source...")
            stopApplication()
            recompileSource(files)
            webApp = makeApplication()
//            config.getServletContext.setAttribute("application", webApp)
            "ok"
          }
          res.getOutputStream.write("Waiting".getBytes("UTF-8"))
        case None =>
          webApp.actions.find(_.path.isMatch(actionPath)) match {
            case Some(action) =>
              log.debug("{} >> {}", pathLog(req) , action)
              val args = ArgsBuilder(req,action)
              val principal = new PrincipalRequest(req)
              val result = action.execute(args, principal)
              ResponseHandler(action, result, req, res)
            case _ => Error(404,"Not Found")
          }
      }
    }
  }

  private[this] def pathLog(req:HttpServletRequest) = new StringBuilder()
          .append(req.getMethod)
          .append(":")
          .append(req.getRequestURI)
          .append(":")
          .append(if(req.getContentType != null) req.getContentType else "" )

//
//  private[this] def callActionOrLogin(req: HttpServletRequest, action: Action, principal: Principal, args: Array[Arg]): AnyRef = {
//    if (webApp.useSsl && action.requiresSsl && !req.isSecure) {
//      val buf = req.getRequestURL
//      // add https and remove the trailing .brzy extension
//      val redirect = buf.replace(0, 4, "https").replace(buf.length() - 5, buf.length(),"").toString
//      log.trace("redirect: {}",redirect)
//      Redirect(redirect)
//    }
//    else if (action.isSecured) {
//      if (req.getSession(false) != null) {
//        log.trace("principal: {}",principal)
//
//        if (action.isAuthorized(principal))
//          action.execute(args, principal)
//        else
//          sendToAuthorization(req)
//      }
//      else {
//        sendToAuthorization(req)
//      }
//    }
//    else {
//      action.execute(args, principal)
//    }
//  }

  /**
   * TODO the redirect path is hard coded here to send them to /auth, that should be configurable
   * some how.
   *
   * @param req The httpServletRequest
   * @return The redirect to the authorization page
   */
  private[this] def sendToAuthorization(req: HttpServletRequest): (Redirect, Flash, Session) = {
    val flash = Flash("Your session has ended. Please login again", "session.end")
    val sessionParam = Session("last_view" -> req.getRequestURI)
    (Redirect("/auth"), flash, sessionParam)
  }

  private[this] def findFiles(root: File): List[File] = {
    if (root.isFile && root.getName.endsWith(".scala"))
      List(root)
    else
      makeList(root.listFiles).flatMap {f => findFiles(f)}
  }

  private[this] def makeList(a: Array[File]): List[File] = {
    if (a == null)
      Nil
    else
      a.toList
  }
//
//  private def cpath: List[URL] = {
//    List(
//      classesDir.toURI.toURL,
//      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/aspectjweaver-1.6.8.jar").toURI.toURL,
//      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/beanwrap-0.2.2.jar").toURI.toURL,
//      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/brzy-webapp-1.0.0.beta3.jar").toURI.toURL,
//      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/commons-fileupload-1.2.2.jar").toURI.toURL,
//      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/commons-io-1.4.jar").toURI.toURL,
//      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/commons-logging-1.1.1.jar").toURI.toURL,
//      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/dom4j-1.6.jar").toURI.toURL,
//      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/fab-configuration-0.8.1.jar").toURI.toURL,
//      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/google-collections-1.0.jar").toURI.toURL,
//      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/gson-1.4.jar").toURI.toURL,
//      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/guava-r09.jar").toURI.toURL,
//      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/ivy-2.2.0.jar").toURI.toURL,
//      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/javassist-3.11.0.GA.jar").toURI.toURL,
//      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/json-1.1.1.jar").toURI.toURL,
//      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/logback-classic-0.9.27.jar").toURI.toURL,
//      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/logback-core-0.9.27.jar").toURI.toURL,
//      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/paranamer-2.3.jar").toURI.toURL,
//      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/reflections-0.9.5-RC2.jar").toURI.toURL,
//      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/scala-compiler-2.8.2.jar").toURI.toURL,
//      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/scala-library-2.8.2.jar").toURI.toURL,
//      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/scalabeans_2.8.1-0.2.jar").toURI.toURL,
//      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/servlet-api-6.0.29.jar").toURI.toURL,
//      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/slf4j-api-1.6.1.jar").toURI.toURL,
//      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/snakeyaml-1.7.jar").toURI.toURL,
//      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/validation-api-1.0.0.GA.jar").toURI.toURL,
//      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/validator-0.1.jar").toURI.toURL,
//      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/xml-apis-1.0.b2.jar").toURI.toURL
//    )
//  }
}
