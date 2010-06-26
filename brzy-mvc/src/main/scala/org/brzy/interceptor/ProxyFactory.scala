package org.brzy.interceptor

import java.lang.reflect.Method
import javassist.util.proxy.{ProxyObject, ProxyFactory => PFactory, MethodFilter}

/**
 * @author Michael Fortin
 * @version $Id: $
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

  def make(clazz:Class[_], args:Array[AnyRef], proxy:MethodInvoker): AnyRef = {
    val factory = new PFactory
    factory.setSuperclass(clazz)
    factory.setFilter(filter)
    factory.create(args.map(a=>{
      if(a.isInstanceOf[ProxyObject]) 
        a.getClass.getSuperclass
      else
        a.getClass
    }), args, proxy)
  }

  def make(clazz:Class[_], proxy:MethodInvoker): AnyRef = make(clazz, Array[AnyRef]() ,proxy)
}