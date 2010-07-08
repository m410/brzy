package org.brzy.squeryl.old

import org.brzy.interceptor.old.{Invocation, Interceptor, Invoker}
import org.brzy.persistence.ThreadScope._
import org.squeryl.Session
import org.squeryl.adapters._
import org.slf4j.LoggerFactory

/**
 *
 * @author Michael Fortin
 * @version $Id: $
 */
class SquerylInterceptor(driver:String, url:String, user:String, pass:String) extends Invoker with Interceptor {

  val log = LoggerFactory.getLogger(getClass)
  log.info("driver: " + driver)
  log.info("url   : " + url)
  log.info("user  : " + user)
  log.info("pass  : " + pass)

  Class.forName(driver)
	val datasource = java.sql.DriverManager.getConnection(url, user, pass)

  val adaptor =
			if(url.indexOf("postgresql") >= 0)
		    new PostgreSqlAdapter
		  else if(url.indexOf("mysql") >= 0)
		    new MySQLAdapter
		  else if(url.indexOf("oracle") >= 0)
		    new OracleAdapter
		  else 
		    new H2Adapter

  override def invoke(invocation: Invocation): AnyRef = {
    val threadContext = SquerylThreadContext(Session.create(datasource, adaptor))
    doWith(threadContext) {
      threadContext.start
      invocation.invoke
    }
  }
}