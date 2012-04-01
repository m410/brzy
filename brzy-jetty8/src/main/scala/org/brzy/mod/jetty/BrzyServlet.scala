/*
 * Copyright 2010 Michael Fortin <mike@brzy.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.brzy.mod.jetty

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
import java.io.{StringWriter, PrintWriter, File}


/**
 * Accepts requests to the application like the Brzy servlet, but checks for changes in
 * the source and recompiles and reloads the application.
 *
 * @author Michael Fortin
 */
class BrzyServlet extends HttpServlet {
  private[this] val log = LoggerFactory.getLogger(classOf[BrzyServlet])
  private[this] var webApp: WebApp = _
  private[this] var appLoader:AppLoader = _
  
  var appState:Future[DynamicAppState] = new Future[DynamicAppState] {
    def isSet = true
    def inputChannel = null
    def apply() = Running(webApp)
    def respond(k: (DynamicAppState) => Unit) {}
  }

  override def init(config: ServletConfig) {
    val sourceDir = new File(config.getInitParameter("source_dir"))
    val classesDir = new File(config.getInitParameter("classes_dir"))
    val classpath = config.getInitParameter("compiler_path")
    val loaderClass = config.getInitParameter("loader_class")

    log.info("source_dir: '{}'", sourceDir)
    log.info("classes_dir: '{}'", classesDir)
    log.info("classpath: '{}'", classpath)
    log.info("app loader class: '{}'", loaderClass)

    appLoader = AppLoader(sourceDir,classesDir,classpath,loaderClass)
    webApp = appLoader.makeApplication()
    config.getServletContext.setAttribute("application",webApp)

    log.info("webApp: '{}'", webApp)
  }

  override def service(req: HttpServletRequest, resp: HttpServletResponse) {
    internal(req, resp)
  }

  private[this] def internal(req: HttpServletRequest, res: HttpServletResponse) {
    log.trace("request: {}, context: {}", req.getServletPath, req.getContextPath)
    val actionPath = ArgsBuilder.parseActionPath(req.getRequestURI, req.getContextPath)
    log.trace("action-path: {}", actionPath)

    if (!appState.isSet) {
      log.warn("Still Compiling Source...")
      render(res)
    }
    else {
      appLoader.sourceModified  match {
        case Some(files) =>
          appState = appLoader.reload(files.asInstanceOf[List[File]])
          render(res)
        case None =>
          if (appState.isSet && appState().isInstanceOf[CompilerError])
            render(res, compilerErrorPage(appState().asInstanceOf[CompilerError].message))
          else
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

  private[this] def render(res:HttpServletResponse, msg:String = "") {
    res.setHeader("Content-Type","text/html")
    
    if (msg == "")
      res.getOutputStream.write(waitPage.getBytes("UTF-8"))
    else
      res.getOutputStream.write(compilerErrorPage(msg).getBytes("UTF-8"))
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

  private[this] val waitPage = """
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="refresh" content="4">
<title>Brzy Recompiling</title>
<style>
body {margin:0 auto;text-align:center;padding:4em 2em;
 font-family:"Helvetica Neue", Arial, Helvetica, sans-serif;color:#333}
</style>
</head>
<body>
<h1>Recompiling Source</h1>
</body>
</html>
"""

  private[this] def compilerErrorPage(msg:String) = {
    new StringBuilder()
    .append("""
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>Brzy Recompiling</title>
<style>
body {margin:0 auto;text-align:center;padding:4em 2em; font-family:"Helvetica Neue", Arial, Helvetica, sans-serif;color:#333}
code {width:980px;text-align:left;background:#eee;color:#000;display:block;padding:10px}
</style>
</head>
<body>
<h1>Compiler Error</h1>
<code>""")
    .append(msg)
    .append("""</code>
</body>
</html>""")
    .toString()
  }
}
