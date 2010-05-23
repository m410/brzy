package org.brzy.interceptor

import java.lang.reflect.Method
import javassist.util.proxy.{ProxyFactory => PFactory, MethodFilter}

/**
 * @author Michael Fortin
 * @version $Id: $
 */
object ProxyFactory {

  private val filter = new MethodFilter {
    def isHandled(m: Method) = !m.getName.equals("finalize") &&
        !m.getName.equals("toString") &&
        !m.getName.equals("clone") &&
        !m.getName.equals("hashCode") &&
        !m.getName.equals("equals")
  }

  def make(clazz:Class[_], args:Array[AnyRef], proxy:MethodInvoker): AnyRef = {
    val factory = new PFactory
    factory.setSuperclass(clazz)
    factory.setFilter(filter)
    factory.create(args.map(_.getClass), args, proxy)
  }

  def make(clazz:Class[_], proxy:MethodInvoker): AnyRef = make(clazz, Array[AnyRef]() ,proxy)
}