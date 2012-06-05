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

import org.brzy.fab.mod.ModProvider
import org.brzy.fab.interceptor.InterceptorProvider
import org.slf4j.LoggerFactory

/**
 * Module Provider for Calista to brzy.
 * 
 * @author Michael Fortin
 */
class CalistaModProvider(c:CalistaModConf) extends ModProvider with InterceptorProvider {
  val log = LoggerFactory.getLogger(classOf[CalistaModProvider])
  val name = c.name.get
  def interceptor = new CalistaContextManager(c)

  override def startup() {
    log.info("Cassandra Version: {}",interceptor.sessionManager.version)
    log.info("Cassandra Cluster Name: {}",interceptor.sessionManager.clusterName)
    log.info("Cassandra Keyspace: {}",interceptor.sessionManager.keyspaceDefinition.logString)
//    if(c.createSchema) {
//      log.info("Creating Schema for keyspace {}",c.keySpace)
//    }
  }
}
