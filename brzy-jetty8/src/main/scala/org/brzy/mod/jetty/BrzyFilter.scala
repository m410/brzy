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

import javax.servlet.{FilterChain, FilterConfig, ServletResponse, ServletRequest, Filter => SFilter}
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

import org.slf4j.LoggerFactory
import java.lang.reflect.Method
import actors.Future
import java.io.File

/**
 * Forwards only requests to brzy actions, lets all other requests pass through.
 *
 * @author Michael Fortin
 */
class BrzyFilter extends SFilter {
  private[this] val log = LoggerFactory.getLogger(classOf[BrzyFilter])
  private[this] var appLoader:AppLoader = _
  private[this] var appState:Future[DynamicAppState] = _


  def init(config: FilterConfig) {
    val sourceDir = new File(config.getInitParameter("source_dir"))
    val classesDir = new File(config.getInitParameter("classes_dir"))
    val compilerPath = config.getInitParameter("compiler_path")
    val runPath = config.getInitParameter("run_path")
    val loaderClass = config.getInitParameter("loader_class")

    log.debug("source_dir: '{}'", sourceDir)
    log.debug("classes_dir: '{}'", classesDir)
    log.debug("compiler_path: '{}'", compilerPath)
    log.debug("run_path: '{}'", runPath)
    log.debug("app loader class: '{}'", loaderClass)

    // TODO this needs to be on the filter, not the servlet
    appLoader = AppLoader(sourceDir,classesDir,compilerPath,runPath,loaderClass)

    if (config.getServletContext.getAttribute("application")!=null) {
      val webApp = config.getServletContext.getAttribute("application")
      log.debug("already initialized webApp: '{}'", webApp)
      appState = initializeTheFuture(webApp)
    }
    else {
      val webApp = appLoader.makeApplication()
      config.getServletContext.setAttribute("application",webApp)
      config.getServletContext.setAttribute("classLoader",appLoader.childClassLoader)
      log.debug("initializing webApp: '{}'", webApp)
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



  def doFilter(req: ServletRequest, r: ServletResponse, chain: FilterChain) {
    val q = req.asInstanceOf[HttpServletRequest]
    val res = r.asInstanceOf[HttpServletResponse]
    log.debug("uri : {}", q.getRequestURI)

    if (appState.isSet) {
      appState() match {
        case Running(wa) =>
          q.getServletContext.setAttribute("application",wa)
          q.getServletContext.setAttribute("classLoader",appLoader.childClassLoader)
          val path = isPathMethod(wa)
          if (path.invoke(wa, q.getContextPath, q.getRequestURI).asInstanceOf[Boolean])
            checkOrCall(res,{()=>wrapTransMethod(wa).invoke(wa, req, res, chain)})
          else
            chain.doFilter(req, res)
        case Compiling =>
          q.getServletContext.setAttribute("application",null)
          render(res)
        case c:CompilerError =>
          q.getServletContext.setAttribute("application",null)
          checkOrCall(res,{()=>render(res, compilerErrorPage(c.message))})
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
      case None =>
        call()
    }
  }


  private[this] def wrapTransMethod(webapp: AnyRef): Method = {
    webapp.getClass.getMethod("wrapWithTransaction", classOf[HttpServletRequest], classOf[HttpServletResponse], classOf[FilterChain])
  }

  private[this] def isPathMethod(webapp: AnyRef): Method = {
    webapp.getClass.getMethod("isPath", classOf[String], classOf[String])
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


  /**
   *
   */
  def destroy() {
    log.trace("Destroy")
  }
}
