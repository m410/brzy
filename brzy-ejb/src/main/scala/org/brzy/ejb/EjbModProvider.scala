package org.brzy.ejb

import org.brzy.config.mod.ModProvider
import collection.mutable.HashMap
import javax.rmi.PortableRemoteObject
import javax.naming.InitialContext

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