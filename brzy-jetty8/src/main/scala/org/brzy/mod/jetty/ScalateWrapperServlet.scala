package org.brzy.mod.jetty

import javax.servlet.http.HttpServlet
import org.fusesource.scalate.servlet.TemplateEngineServlet
import javax.servlet.{ServletConfig, ServletResponse, ServletRequest}

/**
 * Wrapper around scalate servlet that sets the classloader.
 *
 * @author Michael Fortin
 */
class ScalateWrapperServlet extends HttpServlet {

  // todo, need to create class using refection and from the child classloader
  val scalateServlet = new TemplateEngineServlet()


  override def init(config: ServletConfig) {
    scalateServlet.init(config)
    super.init(config)
  }

  override def service(req: ServletRequest, res: ServletResponse) {
    val loader = req.getServletContext.getAttribute("classLoader").asInstanceOf[ClassLoader]
    java.lang.Thread.currentThread().setContextClassLoader(loader)
    scalateServlet.service(req,res)
  }
}
