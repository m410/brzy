package org.brzy.persistence.squeryl

import org.brzy.interceptor.{Invocation, Interceptor, Proxy}
import org.brzy.persistence.ThreadScope._
import org.squeryl.Session
import org.squeryl.adapters._
/**
 *
 * @author Michael Fortin
 * @version $Id: $
 */
class SquerylInterceptor(driver:String, url:String, user:String, pass:String) extends Proxy with Interceptor {

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