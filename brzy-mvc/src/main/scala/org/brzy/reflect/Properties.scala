package org.brzy.reflect

import collection.mutable.HashMap

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
object Properties {

  class BeanProperties(bean:AnyRef) {
    private[this] val ignore = Array("getClass","toString", "equals", "hashCode", "notify", "notifyAll", "wait", "clone", "finalize")

    def properties:Map[String,Any] = {
      val clazz:Class[_] = bean.getClass
      val map = new HashMap[String, Any]()
      clazz.getMethods.foreach(f => {

        if(!ignore.contains(f.getName) && f.getParameterTypes.length == 0)
          map += f.getName -> f.invoke(bean)
      })
      map.toMap
    }
  }

  implicit def propertyOp(bean:AnyRef):BeanProperties = new BeanProperties(bean)
}