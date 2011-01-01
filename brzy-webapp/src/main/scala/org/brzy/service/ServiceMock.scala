package org.brzy.service

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
trait ServiceMock {
  def serviceName = {
    val name = this.getClass.getSimpleName
    name.substring(0,1).toLowerCase + name.substring(1,name.length)
  }
}