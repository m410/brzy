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
import org.brzy.calista.ocm.Calista

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
class CalistaContextManager(c: CalistaModConf) extends ManagedThreadContext {
  type T = Option[Session]
  val empty: T = None
  val context = Calista

  val sessionManager = new SessionManager(keyspace = c.keySpace.get, url = c.host.get, port = c.port.get)

  def destroySession(target: Option[Session]) = {
    target.get.close
    context.value = empty
  }

  def createSession = Option(sessionManager.createSession)
}
