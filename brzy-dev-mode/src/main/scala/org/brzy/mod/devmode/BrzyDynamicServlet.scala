package org.brzy.mod.devmode

import java.io.{File}
import java.net.URLClassLoader
import javax.servlet.{ServletResponse, ServletRequest, ServletConfig}
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}

import org.slf4j.LoggerFactory
import actors.Futures._
import actors.Future

import tools.nsc.reporters.ConsoleReporter
import tools.nsc.{Global, Settings}

import org.brzy.webapp.action.Action
import org.brzy.application.WebApp
import org.brzy.webapp.action.response.{Error, Flash, Session, Redirect, ResponseHandler}
import org.brzy.webapp.action.args.{Principal, Arg, PrincipalRequest, ArgsBuilder}


/**
 * Accepts requests to the application like the Brzy servlet, but checks for changes in
 * the source and recompiles and reloads the application.
 *
 * @author Michael Fortin
 */
class BrzyDynamicServlet extends HttpServlet {
  private[this] val log = LoggerFactory.getLogger(classOf[BrzyDynamicServlet])
  private[this] var applicationLoader: URLClassLoader = _
  private[this] var webApp: WebApp = _
  private[this] var lastModified = System.currentTimeMillis() - 1000 // need to round to the previous second
  private[this] var classpath:String = _
  private[this] var sourceDir:File = _
  private[this] var classesDir:File = _

  var appState:Future[DynamicAppState] = new Future[DynamicAppState] {
    def isSet = true
    def inputChannel = null
    def apply() = Running
    def respond(k: (DynamicAppState) => Unit) {}
  }

  override def init(config: ServletConfig) {
    sourceDir = new File(config.getInitParameter("source_dir"))
    classesDir = new File(config.getInitParameter("classes_dir"))
    classpath = config.getInitParameter("compiler_path")
    webApp = config.getServletContext.getAttribute("application").asInstanceOf[WebApp]
    log.info("source_dir: '{}'", sourceDir)
    log.info("classes_dir: '{}'", classesDir)
    log.info("classpath: '{}'", classpath)
    log.info("last modified: '{}'", lastModified)
    
    if(webApp == null)
      webApp = makeApplication()
    
    log.info("webApp: '{}'", webApp)
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
    println("##### build new classloader")
    applicationLoader = new URLClassLoader(cp, getClass.getClassLoader)
    val clazz = applicationLoader.loadClass("org.brzy.test.ApplicationLoader")
    val declaredConstructor = clazz.getDeclaredConstructor(Array.empty[Class[_]]: _*)
    declaredConstructor.setAccessible(true)
    val inst = declaredConstructor.newInstance()
    val method = clazz.getMethod("load", Array.empty[Class[_]]: _*)
    val a = method.invoke(inst)
    a.getClass.getMethod("startup").invoke(a)
    println("##### started app")

    a.asInstanceOf[WebApp]
  }

  private[this] def recompileSource(files: List[File]) {
    val buf = new StringBuilder()
    def error(s: String) {
      buf.append(s)
    }
    val settings = new Settings(error)
    settings.classpath.value = classpath
    settings.sourcedir.value = sourceDir.getAbsolutePath
    settings.outdir.value = classesDir.getAbsolutePath
    settings.deprecation.value = true // enable detailed deprecation warnings
    settings.unchecked.value = true // enable detailed unchecked warnings

    println("##### start compile")
    val reporter = new ConsoleReporter(settings)
    val compiler = new Global(settings, reporter)
    (new compiler.Run).compile(files.map(_.getAbsolutePath))
    println("##### done compile")
    if (reporter.hasErrors || reporter.WARNING.count > 0) {
      reporter.printSummary()
    }
    println("##### compiler errors: '" + buf.toString()+"'")
    buf.toString()
  }

  private[this] def stopApplication() {
    webApp.shutdown()
    webApp = null
  }

  private[this] def internal(req: HttpServletRequest, res: HttpServletResponse) {
    log.trace("request: {}, context: {}", req.getServletPath, req.getContextPath)
    val actionPath = ArgsBuilder.parseActionPath(req.getRequestURI, req.getContextPath)
    log.trace("action-path: {}", actionPath)



    if (!appState.isSet) {
      log.warn("Still Compiling Source...")
      renderWait(res)
    }
    else {
      sourceModified  match {
        case Some(files) =>
          appState = future {
            lastModified = System.currentTimeMillis() - 1000
            log.warn("Recompiling Source...")
            stopApplication()
            recompileSource(files)
            webApp = makeApplication()
            req.getSession.getServletContext.setAttribute("application", webApp)
            Running // todo replace with compiler errors
          }
          renderWait(res)
        case None =>
          webApp.actions.find(_.path.isMatch(actionPath)) match {
            case Some(action) =>
              log.debug("{} >> {}", pathLog(req) , action)
              val args = ArgsBuilder(req,action)
              val principal = new PrincipalRequest(req)
              val result = callActionOrLogin(req,action,principal,args)
              ResponseHandler(action, result, req, res)
            case _ => Error(404,"Not Found")
          }
      }
    }
  }

  private[this] def renderWait(res:HttpServletResponse) {
    res.setHeader("Content-Type","text/html")
    res.getOutputStream.write(waitPage.getBytes("UTF-8"))
  }

  private[this] def pathLog(req:HttpServletRequest) = new StringBuilder()
          .append(req.getMethod)
          .append(":")
          .append(req.getRequestURI)
          .append(":")
          .append(if(req.getContentType != null) req.getContentType else "" )


  private[this] def callActionOrLogin(req: HttpServletRequest, action: Action, principal: Principal, args: Array[Arg]): AnyRef = {
    if (webApp.useSsl && action.requiresSsl && !req.isSecure) {
      val buf = req.getRequestURL
      // add https and remove the trailing .brzy extension
      val redirect = buf.replace(0, 4, "https").replace(buf.length() - 5, buf.length(),"").toString
      log.trace("redirect: {}",redirect)
      Redirect(redirect)
    }
    else if (action.isSecured) {
      if (req.getSession(false) != null) {
        log.trace("principal: {}",principal)

        if (action.isAuthorized(principal))
          action.execute(args, principal)
        else
          sendToAuthorization(req)
      }
      else {
        sendToAuthorization(req)
      }
    }
    else {
      action.execute(args, principal)
    }
  }

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

  private[this] val waitPage = """
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="refresh" content="4">
<title>Brzy Recompiling</title>
<style>
body {margin:0 auto;text-align:center;padding:4em 2em;
 font-family:"Helvetica Neue", Arial, Helvetica, sans-serif;color:#666}
</style>
</head>
<body>
<h1>Recompiling Source</h1>
</body>
</html>
"""

}
