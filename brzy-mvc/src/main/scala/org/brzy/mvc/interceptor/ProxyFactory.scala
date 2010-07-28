package org.brzy.mvc.interceptor

import java.lang.reflect.Method
import javassist.util.proxy.{ProxyObject, ProxyFactory => PFactory, MethodFilter}

/**
 * @author Michael Fortin
 */
object ProxyFactory {

  private val filter = new MethodFilter {
    def isHandled(m: Method) =
        !m.getName.equals("clone") &&
        !m.getName.equals("equals") &&
        !m.getName.equals("finalize") &&
        !m.getName.equals("hashCode") &&
        !m.getName.equals("notify") &&
        !m.getName.equals("notifyAll") &&
        !m.getName.equals("toString") &&
        !m.getName.equals("wait")
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