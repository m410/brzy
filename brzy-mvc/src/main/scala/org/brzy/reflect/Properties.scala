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
package org.brzy.reflect

import collection.mutable.HashMap

/**
 * This is created to allow access to the properties of an object like using the java beans
 * api.  Once implicitly added to scope:
 * <pre>
 * implicit Properties._
 * </pre>
 * you can call getters on a scala object like
 * <pre>
 * val mybean = new MyBean
 * val nameValue = mybean.properties("name")
 * </pre>
 * The underlying map is immutable so setting a value will do nothing.
 * <p>
 * todo: eventually, it should be able to set values on var's and filter out functions that are
 * not in getter setter pairs.
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