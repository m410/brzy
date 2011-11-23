package org.brzy.service

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
class Service {

  def serviceName = {
    val name = this.getClass.getSimpleName

    if (name.indexOf("_$$_javassist") != -1)
      name.substring(0, 1).toLowerCase + name.substring(1, name.indexOf("_$$_javassist"))
    else
      name.substring(0, 1).toLowerCase + name.substring(1, name.length)
  }

  def initializeService() {}

  def destroyService() {}
}