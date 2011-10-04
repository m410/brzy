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
package org.brzy.mod.calista

import org.brzy.calista.Session
import org.brzy.calista.SessionManager

import org.brzy.fab.interceptor.ManagedThreadContext
import org.brzy.calista.Calista

/**
 * Provided by the Module, this manages session creation and destruction for connecting to
 * a Cassandra datastore using Calista.  It's used to managed a ThreadLocal variable for each
 * request.
 *
 * @author Michael Fortin
 */
class CalistaContextManager(c: CalistaModConf) extends ManagedThreadContext {
  type T = Session
  val empty: T = null
  val context = Calista

  val sessionManager = new SessionManager(c.keySpace.get, c.host.get, c.port.get)

  def destroySession(target: Session) {
    target.close()
    context.value = empty
  }

  def createSession = sessionManager.createSession
}