package org.brzy.webapp.mock

import org.brzy.fab.mod.ModProvider
import org.slf4j.LoggerFactory
import java.sql.DriverManager
import collection.JavaConversions._
import org.brzy.fab.interceptor.{ManagedThreadContext, InterceptorProvider}
import util.DynamicVariable

class MockSquerylModProvider(c:MockSquerylModConfig) extends ModProvider with InterceptorProvider {

  private val log = LoggerFactory.getLogger(getClass)
  val name = c.name.get

  log.debug("db driver: {}",c.driver.getOrElse("?"))
  log.debug("db url   : {}",c.url.getOrElse("?"))
  log.debug("db user  : {}",c.userName.getOrElse("?"))
  log.debug("db passwd: {}",c.password.getOrElse("?"))

  class Session {

  }

  override def interceptor = new ManagedThreadContext {
    type T = Session

    def empty = new Session()

    def context = new DynamicVariable(empty)

    def createSession = new Session()

    def destroySession(target: this.type#T) {}
  }

  /**
   * Deregister all drivers that the DriverManager is aware of.
   */
  override def shutdown() {
    log.debug("shutdown")
    val drivers = DriverManager.getDrivers
    drivers.foreach(d=>{
      log.debug("deregister: {}",d)
      DriverManager.deregisterDriver(d)
    })
  }
  override def startup()  {
    log.debug("startup")
  }

}