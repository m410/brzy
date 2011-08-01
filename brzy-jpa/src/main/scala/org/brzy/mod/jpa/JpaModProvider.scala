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
package org.brzy.mod.jpa

import org.brzy.fab.mod.ModProvider
import org.brzy.fab.interceptor.InterceptorProvider
import java.sql.DriverManager
import collection.JavaConversions._
import org.slf4j.LoggerFactory

/**
 * JPA module provider.  This adds the jpa context manager to the application as the interceptor.
 * 
 * @author Michael Fortin
 */
class JpaModProvider(c:JpaModConfig) extends ModProvider with InterceptorProvider {
  val log = LoggerFactory.getLogger(getClass)
  val interceptor = new JpaContextManager(c.persistenceUnit.get)
  val name = c.name.get

  /**
   * Deregister all drivers that the DriverManager is aware of.
   */
  override def shutdown {
    val drivers = DriverManager.getDrivers
    drivers.foreach(d=>{
      log.debug("deregister: {}",d)
      DriverManager.deregisterDriver(d)
    })
  }
}