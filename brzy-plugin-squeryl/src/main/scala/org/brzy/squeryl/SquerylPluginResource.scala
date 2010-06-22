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
  log.debug("db driver: {}",c.driver.getOrElse("?"))
  log.debug("db url   : {}",c.url.getOrElse("?"))
  log.debug("db user  : {}",c.userName.getOrElse("?"))
  log.debug("db passwd: {}",c.password.getOrElse("?"))

  assert(c.driver.isDefined, "the database driver is not defined.")
  assert(c.driver.get != null, "the database driver can not be null.")
  assert(c.url.isDefined, "the database url is not defined.")
  assert(c.url.get != null, "the database url can not be null.")
  assert(c.userName.isDefined, "the database userName is not defined.")
  assert(c.userName.get != null, "the database userName can not be null.")
  assert(c.password.isDefined, "the database password is not defined.")
  assert(c.password.get != null, "the database password can not be null.")

  override def interceptor = {
    new SquerylInterceptor(c.driver.get, c.url.get, c.userName.get, c.password.get)
  }

  override def services = Nil

  override def shutdown = {
    log.debug("shutdown")
  }

  override def startup = {
    log.debug("startup")
  }

}