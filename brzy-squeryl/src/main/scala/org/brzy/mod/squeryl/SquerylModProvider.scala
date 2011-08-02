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
package org.brzy.mod.squeryl

import org.slf4j.LoggerFactory
import org.brzy.fab.mod.ModProvider
import org.brzy.fab.interceptor.InterceptorProvider
import java.sql.DriverManager

/**
 * Squeryl database persistence module provider.
 * 
 * @author Michael Fortin
 */
class SquerylModProvider(c:SquerylModConfig) extends ModProvider with InterceptorProvider {

  private val log = LoggerFactory.getLogger(getClass)
  val name = c.name.get

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

  override def interceptor = new SquerylContextManager(c.driver.get,c.url.get,c.userName.get,c.password.get)

  /**
   * Deregister all drivers that the DriverManager is aware of.
   */
  override def shutdown {
    log.debug("shutdown")
    val drivers = DriverManager.getDrivers
    drivers.foreach(d=>{
      log.debug("deregister: {}",d)
      DriverManager.deregisterDriver(d)
    })
  }
  override def startup = {
    log.debug("startup")
  }

}