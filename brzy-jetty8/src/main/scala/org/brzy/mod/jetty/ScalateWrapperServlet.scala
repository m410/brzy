package org.brzy.mod.jetty

import javax.servlet.http.HttpServlet
import javax.servlet.{ServletConfig, ServletResponse, ServletRequest}
import java.lang.reflect.InvocationTargetException

/**
 * Wrapper around scalate servlet that sets the classloader.
 *
 * @author Michael Fortin
 */
class ScalateWrapperServlet extends HttpServlet {

  private[this] var servletConfig: ServletConfig = _
  private[this] var scalateServletClass: Class[_] = _
  private[this] var scalateServletInst: AnyRef = _
  private[this] var classLoader: ClassLoader = _

  private[this] val ReqCls = classOf[ServletRequest]
  private[this] val resCls = classOf[ServletResponse]
  private[this] val configCls = classOf[ServletConfig]
  private[this] val scalateServletClassName = "org.fusesource.scalate.servlet.TemplateEngineServlet"


  override def init(config: ServletConfig) {
    servletConfig = config
    super.init(config)
  }

  override def service(req: ServletRequest, res: ServletResponse) {
    val loader = req.getServletContext.getAttribute("classLoader").asInstanceOf[ClassLoader]
    Thread.currentThread().setContextClassLoader(loader)

    if (scalateServletInst == null || classLoader == null || loader != classLoader) {
      classLoader = loader
      scalateServletClass = classLoader.loadClass(scalateServletClassName)
      scalateServletInst = scalateServletClass.newInstance().asInstanceOf[AnyRef]
      scalateServletClass.getMethod("init", configCls).invoke(scalateServletInst, servletConfig)
    }

    try {
      scalateServletClass.getMethod("service", ReqCls, resCls).invoke(scalateServletInst, req, res)
    }
    catch {
      case i: InvocationTargetException => throw i.getCause
    }
  }
}
