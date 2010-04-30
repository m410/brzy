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

  def make[T](clazz:Class[T], args:Array[AnyRef], proxy:Proxy): T = {
    val factory = new PFactory
    factory.setSuperclass(clazz)
    factory.setFilter(filter)
    factory.create(args.map(_.getClass), args, proxy).asInstanceOf[T]
  }

  def make[T](clazz:Class[T], proxy:Proxy): T = make(clazz, Array[AnyRef]() ,proxy)
}