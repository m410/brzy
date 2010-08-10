package org.brzy.ejb

import java.beans.ConstructorProperties

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
@ConstructorProperties(Array("serviceName","remoteInterface","jndiName"))
case class EjbBean(serviceName:String,remoteInterface:String,jndiName:String)