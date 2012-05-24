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

import javax.servlet.ServletConfig
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}

import org.slf4j.LoggerFactory
import actors.Future

import java.io.File
import java.lang.reflect.Method


/**
 * Accepts requests to the application like the Brzy servlet, but checks for changes in
 * the source and recompiles and reloads the application.
 *
 * @author Michael Fortin
 */
class BrzyServlet extends HttpServlet {
  private[this] val log = LoggerFactory.getLogger(classOf[BrzyServlet])
  private[this] var appLoader:AppLoader = _
  private[this] var appState:Future[DynamicAppState] = _

  override def init(config: ServletConfig) {
    val sourceDir = new File(config.getInitParameter("source_dir"))
    val classesDir = new File(config.getInitParameter("classes_dir"))
    val compilerPath = config.getInitParameter("compiler_path")
    val runPath = config.getInitParameter("run_path")
    val loaderClass = config.getInitParameter("loader_class")

    log.info("source_dir: '{}'", sourceDir)
    log.info("classes_dir: '{}'", classesDir)
    log.info("compiler_path: '{}'", compilerPath)
    log.info("run_path: '{}'", runPath)
    log.info("app loader class: '{}'", loaderClass)

    // TODO this needs to be on the filter, not the servlet
    appLoader = AppLoader(sourceDir,classesDir,compilerPath,runPath,loaderClass)

    if (config.getServletContext.getAttribute("application")!=null) {
      val webApp = config.getServletContext.getAttribute("application")
      log.info("already initialized webApp: '{}'", webApp)
      appState = initializeTheFuture(webApp)
    }
    else {
      val webApp = appLoader.makeApplication()
      config.getServletContext.setAttribute("application",webApp)
      log.info("initializing webApp: '{}'", webApp)
      appState = initializeTheFuture(webApp)
    }
  }


  private[this] def initializeTheFuture(webApp: AnyRef): Future[DynamicAppState]  = {
    new Future[DynamicAppState] {
      def isSet = true
      def inputChannel = null
      def apply() = Running(webApp)
      def respond(k: (DynamicAppState) => Unit) {}
    }
  }

  override def service(req: HttpServletRequest, resp: HttpServletResponse) {
    internal(req, resp)
  }

  private[this] def internal(req: HttpServletRequest, res: HttpServletResponse) {
    log.trace("request: {}, context: {}", req.getServletPath, req.getContextPath)

    if (appState.isSet) {
      appState() match {
        case Running(wa) => checkOrCall(res,{()=>callActionMethod(wa).invoke(wa,req,res)})
        case Compiling => render(res)
        case c:CompilerError => checkOrCall(res,{()=>render(res, compilerErrorPage(c.message))})
      }
    }
    else {
      log.warn("Still Compiling Source...")
      render(res)
    }
  }

  private[this] def checkOrCall(res: HttpServletResponse, call:()=>Unit) {
    appLoader.sourceModified  match {
      case Some(files) =>
        appState = appLoader.reload(files.asInstanceOf[List[File]])
        render(res)
      case None => call()
    }
  }

  private[this] def callActionMethod(wa: AnyRef): Method = {
    wa.getClass.getMethod("callAction", classOf[HttpServletRequest], classOf[HttpServletResponse])
  }

  private[this] def render(res:HttpServletResponse, msg:String = "") {
    res.setHeader("Content-Type","text/html")
    
    if (msg == "")
      res.getOutputStream.write(waitPage.getBytes("UTF-8"))
    else
      res.getOutputStream.write(compilerErrorPage(msg).getBytes("UTF-8"))
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
