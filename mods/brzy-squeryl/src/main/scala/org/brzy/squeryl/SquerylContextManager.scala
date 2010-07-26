package org.brzy.squeryl

import org.squeryl.internals.DatabaseAdapter
import java.lang.reflect.Method
import org.brzy.mvc.interceptor.{MethodMatcher, ContextFactory, ManagedThreadContext}
import org.squeryl.{SessionFactory, Session}
import org.squeryl.adapters.{H2Adapter, PostgreSqlAdapter, MySQLAdapter, OracleAdapter}
import java.sql.{DriverManager, Connection}
import util.DynamicVariable

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
class SquerylContextManager(driver:String, url:String, usr:String, pass:String) extends ManagedThreadContext {

  Class.forName(driver)
  type T = Option[Session]

  val adaptor:DatabaseAdapter =
      if(url.contains("jdbc:oracle")) new OracleAdapter
      else if(url.contains("jdbc:postgresql")) new PostgreSqlAdapter
      else if(url.contains("jdbc:mysql")) new MySQLAdapter
      else if(url.contains("jdbc:h2")) new H2Adapter
      else error("no adapter found for url:" + url)


  val empty:T = None
  val context = new DynamicVariable(empty)
  SessionFactory.externalTransactionManagementAdapter = Some(()=> {
    context.value.getOrElse(error("Session was not initialized.  You may be out of transaction scope."))
  })

  val matcher = new MethodMatcher {
    def isMatch(a: AnyRef, m: Method) = true
  }

  val factory =  new ContextFactory[T] {
    def destroy(s: T) = s.get.close
    def create = Some(Session.create(DriverManager.getConnection(url,usr,pass),adaptor))
  }
}