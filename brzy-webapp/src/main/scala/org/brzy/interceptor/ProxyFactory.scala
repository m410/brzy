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
package org.brzy.interceptor

import java.lang.reflect.Method
import javassist.util.proxy.{ProxyObject, ProxyFactory => PFactory, MethodFilter}

/**
 * Creates an instance of a javassist proxy around a controller or service.
 * @author Michael Fortin
 */
object ProxyFactory {

  private val filter = new MethodFilter {
    def isHandled(m: Method) =
        !m.getName.equals("toString") &&
        !m.getName.equals("equals") &&
        !m.getName.equals("hashCode") &&
        !m.getName.equals("notify") &&
        !m.getName.equals("notifyAll") &&
        !m.getName.equals("wait") &&
        !m.getName.equals("clone") &&
        !m.getName.equals("finalize")
  }

  def make(clazz:Class[_], args:Array[AnyRef], proxy:Invoker): AnyRef = {
    val factory = new PFactory
    factory.setSuperclass(clazz)
    factory.setFilter(filter)
    val paramTypes = args.map(a => {
      if (a.isInstanceOf[ProxyObject])
        a.getClass.getSuperclass
      else
        a.getClass
    })
    factory.create(paramTypes, args, proxy)
  }

  def make(clazz:Class[_], proxy:Invoker): AnyRef = make(clazz, Array[AnyRef]() ,proxy)

  def isProxy(a:AnyRef):Boolean = a.isInstanceOf[ProxyObject] 
}