package org.brzy.webapp.service

/**
 * This trait can be applied to service classes to make is automatically discoverable at
 * application startup.
 *
 * @author Michael Fortin
 */
trait Service {

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