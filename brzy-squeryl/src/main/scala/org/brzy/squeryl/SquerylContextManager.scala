package org.brzy.squeryl

import org.brzy.mvc.interceptor.ManagedThreadContext

import org.squeryl.internals.DatabaseAdapter
import org.squeryl.{SessionFactory, Session}
import org.squeryl.adapters._

import java.sql.DriverManager
import util.DynamicVariable
import org.slf4j.LoggerFactory

/**
 * Manages transactions for the Squeryl ORM with a ThreadLocal variable.
 * 
 * @author Michael Fortin
 */
class SquerylContextManager(driver:String, url:String, usr:String, pass:String) extends ManagedThreadContext {
  private[this] val log = LoggerFactory.getLogger(classOf[SquerylContextManager])
  Class.forName(driver)
  type T = Option[Session]

  val adaptor:DatabaseAdapter =
      if(url.contains("jdbc:oracle")) new OracleAdapter
      else if(url.contains("jdbc:postgresql")) new PostgreSqlAdapter
      else if(url.contains("jdbc:mysql")) new MySQLAdapter
      else if(url.contains("jdbc:h2")) new H2Adapter
      else error("no adapter found for driver:" + url)

  val empty:T = None
  val context = new DynamicVariable(empty)

  // this allows client api to use the squeryl Session object
  SessionFactory.externalTransactionManagementAdapter = Some(()=> {
    context.value.getOrElse(error("Session was not initialized.  You may be out of transaction scope."))
  })

  def destroySession(s: T) = {
    log.trace("Destroy Session: {}",s)
    s.get.connection.commit
    s.get.close
  }

  def createSession = {
    log.trace("Create Session")
    Some(Session.create(DriverManager.getConnection(url,usr,pass),adaptor))
  }
}