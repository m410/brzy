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


import org.squeryl.internals.DatabaseAdapter
import org.squeryl.{SessionFactory, Session}
import org.squeryl.adapters._

import util.DynamicVariable
import org.slf4j.LoggerFactory
import org.brzy.fab.interceptor.ManagedThreadContext
import java.sql.{Connection, DriverManager}

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
    val connection: Connection = DriverManager.getConnection(url, usr, pass)
    connection.setAutoCommit(false)
    val sess:Session = Session.create(connection, adaptor)

    if(log.isTraceEnabled)
      sess.setLogger((t:String)=>{log.trace("sql: {}", t)})
    
    Some(sess)
  }
}