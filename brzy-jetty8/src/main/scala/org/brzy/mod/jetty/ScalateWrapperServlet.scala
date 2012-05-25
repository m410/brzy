package org.brzy.mod.jetty

import javax.servlet.http.HttpServlet
import javax.servlet.{ServletConfig, ServletResponse, ServletRequest}

/**
 * Wrapper around scalate servlet that sets the classloader.
 *
 * @author Michael Fortin
 */
class ScalateWrapperServlet extends HttpServlet {

  var servletConfig:ServletConfig = _

  override def init(config: ServletConfig) {
    servletConfig = config
    super.init(config)
  }

  override def service(req: ServletRequest, res: ServletResponse) {
    val loader = req.getServletContext.getAttribute("classLoader").asInstanceOf[ClassLoader]
    val templateClass = loader.loadClass("org.fusesource.scalate.servlet.TemplateEngineServlet")
    val instance = templateClass.newInstance()
    templateClass.getMethod("init",classOf[ServletConfig]).invoke(instance,servletConfig)
    templateClass.getMethod("service",classOf[ServletRequest],classOf[ServletResponse]).invoke(instance,req,res)
  }
}
