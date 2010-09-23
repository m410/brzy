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
package org.brzy.ejb

import collection.mutable.HashMap
import javax.rmi.PortableRemoteObject
import javax.naming.InitialContext
import org.brzy.fab.mod.ModProvider

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
class EjbModProvider(c:EjbModConfig) extends ModProvider {
  val name = c.name.get
  val contextProperties = {
    val props = new java.util.Properties
    props
  }
  val initialContext = new InitialContext()

  override val serviceMap = {
    val map = HashMap[String,AnyRef]()
    c.beans.foreach(b => {
      val interfaceClass = Class.forName(b.remoteInterface)
      val ejbHome:java.lang.Object  = initialContext.lookup(b.jndiName)
      val ref = PortableRemoteObject.narrow(ejbHome, interfaceClass)
      map += (b.serviceName -> ref)
    })
    map.toMap
  }
}