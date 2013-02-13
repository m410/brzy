package org.brzy.mock

import org.brzy.fab.mod.ModProvider
import org.slf4j.LoggerFactory
import java.sql.DriverManager
import collection.JavaConversions._
import util.DynamicVariable
import org.brzy.webapp.persistence.{SessionFactory, SessionFactoryProvider}
import org.brzy.webapp.persistence.Isolation._

class MockSquerylModProvider(c:MockSquerylModConfig) extends ModProvider with SessionFactoryProvider {

  private val log = LoggerFactory.getLogger(getClass)
  val name = c.name.get

  log.debug("db driver: {}",c.driver.getOrElse("?"))
  log.debug("db url   : {}",c.url.getOrElse("?"))
  log.debug("db user  : {}",c.userName.getOrElse("?"))
  log.debug("db passwd: {}",c.password.getOrElse("?"))

  class Session {

  }

  override def sessionFactory = new SessionFactory {
    type T = Session

    def empty = new Session()

    def context = new DynamicVariable(empty)

    def createSession(iso:Isolation, readOnly:Boolean) = new Session()

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