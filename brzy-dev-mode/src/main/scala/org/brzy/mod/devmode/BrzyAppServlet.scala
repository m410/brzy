package org.brzy.mod.devmode

import java.io.{PrintWriter, File}
import java.net.{URL, URLClassLoader}
import org.brzy.webapp.BrzyServlet
import javax.servlet.{ServletResponse, ServletRequest, ServletConfig}
import org.brzy.application.WebApp
import tools.nsc.reporters.ConsoleReporter
import tools.nsc.{Global, Settings}
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import java.util.Date
import org.brzy.webapp.action.args.{Principal, Arg, PrincipalRequest, ArgsBuilder}
import org.brzy.webapp.action.Action
import org.brzy.webapp.action.response.{Flash, Session, Redirect, ResponseHandler}
import org.slf4j.LoggerFactory

/**
 * Accepts requests to the application like the Brzy servlet, but checks for changes in
 * the source and recompiles and reloads the application.
 *
 * @author Michael Fortin
 */
class BrzyAppServlet extends HttpServlet {
  private[this] val log = LoggerFactory.getLogger(classOf[BrzyAppServlet])
  var applicationLoader: URLClassLoader = _
  var webApp: AnyRef = _
  var lastModified = System.currentTimeMillis()
  var classpath:List[URL] = _
  var sourceDir = new File(new File("src"),"scala")
  var classesDir = new File("target")

  override def init(config: ServletConfig) {
    sourceDir = new File(config.getInitParameter("source_dir"))
    classesDir = new File(config.getInitParameter("classes_dir"))
    classpath = cpath

    webApp = makeApplication()
    config.getServletContext.setAttribute("application", webApp)
  }

  override def service(req: ServletRequest, res: ServletResponse) {
    internal(req.asInstanceOf[HttpServletRequest], res.asInstanceOf[HttpServletResponse])
  }
  
  def sourceModified: Option[List[File]] = {
    val files = findFiles(sourceDir).filter(_.lastModified() > lastModified)
    
    if (files.isEmpty) {
      None
    }
    else {
      lastModified = System.currentTimeMillis()
      Option(files)
    }
  }

  def makeApplication() = {
    applicationLoader = new URLClassLoader(classpath.toArray, getClass.getClassLoader.getParent)
    val clazz = applicationLoader.loadClass("org.brzy.application.WebApp$")
    println(clazz.getClassLoader)
    val declaredConstructor = clazz.getDeclaredConstructor(Array.empty[Class[_]]: _*)
    declaredConstructor.setAccessible(true)
    val inst = declaredConstructor.newInstance()
    println(inst.asInstanceOf[AnyRef].getClass.getClassLoader)
    val method = clazz.getMethod("apply", classOf[String])
    val a = method.invoke(inst,"development")
    a.getClass.getMethod("startup").invoke(a,null)
    a
  }

  def recompileSource(files: List[File]) {
    def error(s: String) {
      println(s)
    }
    val settings = new Settings(error)
//    settings.classpath.value = classpath.map(_.getAbsolutePath)
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

  def stopApplication() {
    webApp.getClass.getMethod("shutdown").invoke(webApp,null)
//    webApp.shutdown()
  }

//  sourceModified match {
//    case Some(files) =>
//      stopApplication()
//      recompileSource(files)
//      app = makeApplication()
//      //        config.getServletContext.setAttribute("application", a)
//      super.service(req, res)
//    case None =>
//      super.service(req, res)
//  }

  private[this]  def internal(req: HttpServletRequest, res: HttpServletResponse) {
    log.trace("request: {}, context: {}", req.getServletPath, req.getContextPath)
    val actionPath = ArgsBuilder.parseActionPath(req.getRequestURI, req.getContextPath)
    log.trace("action-path: {}", actionPath)

//    webApp.actions.find(_.path.isMatch(actionPath)) match {
//      case Some(action) =>
//        log.debug("{} >> {}", pathLog(req) , action)
//        val args = ArgsBuilder(req,action)
//        val principal = new PrincipalRequest(req)
//
//
//        if (!action.isConstrained(req)) {
//          val result = callActionOrLogin(req, action, principal, args)
//          ResponseHandler(action, result, req, res)
//        }
//        else {
//          res.sendError(500)
//        }
//      case _ =>
//        res.sendError(404)
//    }
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

  private def cpath: List[URL] = {
    List(
      classesDir.toURI.toURL,
      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/aspectjweaver-1.6.8.jar").toURI.toURL,
      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/beanwrap-0.2.2.jar").toURI.toURL,
      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/brzy-webapp-1.0.0.beta3.jar").toURI.toURL,
      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/commons-fileupload-1.2.2.jar").toURI.toURL,
      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/commons-io-1.4.jar").toURI.toURL,
      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/commons-logging-1.1.1.jar").toURI.toURL,
      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/dom4j-1.6.jar").toURI.toURL,
      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/fab-configuration-0.8.1.jar").toURI.toURL,
      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/google-collections-1.0.jar").toURI.toURL,
      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/gson-1.4.jar").toURI.toURL,
      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/guava-r09.jar").toURI.toURL,
      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/ivy-2.2.0.jar").toURI.toURL,
      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/javassist-3.11.0.GA.jar").toURI.toURL,
      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/json-1.1.1.jar").toURI.toURL,
      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/logback-classic-0.9.27.jar").toURI.toURL,
      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/logback-core-0.9.27.jar").toURI.toURL,
      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/paranamer-2.3.jar").toURI.toURL,
      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/reflections-0.9.5-RC2.jar").toURI.toURL,
      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/scala-compiler-2.8.2.jar").toURI.toURL,
      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/scala-library-2.8.2.jar").toURI.toURL,
      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/scalabeans_2.8.1-0.2.jar").toURI.toURL,
      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/servlet-api-6.0.29.jar").toURI.toURL,
      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/slf4j-api-1.6.1.jar").toURI.toURL,
      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/snakeyaml-1.7.jar").toURI.toURL,
      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/validation-api-1.0.0.GA.jar").toURI.toURL,
      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/validator-0.1.jar").toURI.toURL,
      new File("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/xml-apis-1.0.b2.jar").toURI.toURL
    )
  }
}
