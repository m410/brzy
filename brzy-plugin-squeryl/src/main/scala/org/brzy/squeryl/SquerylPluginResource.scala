package org.brzy.squeryl

import org.brzy.config.plugin.PluginResource
import org.slf4j.LoggerFactory
import org.brzy.interceptor.InterceptorResource

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
class SquerylPluginResource(c:SquerylPluginConfig) extends PluginResource with InterceptorResource {

  private val log = LoggerFactory.getLogger(getClass)

  override def interceptor = {
    new SquerylInterceptor(c.driver.get, c.url.get, c.userName.get, c.password.get)
  }

  override def services = Nil

  override def shutdown = {
    log.debug("shutdown")
  }

  override def startup = {
    log.debug("startu")
  }

}