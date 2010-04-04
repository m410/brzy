package org.brzy.trx

import java.lang.Class
import javassist.util.proxy.{MethodFilter, ProxyObject, ProxyFactory, MethodHandler}
import java.lang.reflect.Method

/**
 * @author Michael Fortin
 * @version $Id: $
 */
object ProxyBuilder {

  def proxy[T<:Object](clazz:Class[T], interceptor:MethodHandler, args:Array[AnyRef]):T = {
    val factory = new ProxyFactory
    factory.setSuperclass(clazz)
    factory.setFilter(new MethodFilter {
      def isHandled(m:Method ) =  !m.getName.equals("finalize") &&
          !m.getName.equals("toString") &&
          !m.getName.equals("clone") &&
          !m.getName.equals("hashCode") &&
          !m.getName.equals("equals")
    })

//    val targetClass = factory.createClass
//    val targetInstance = targetClass.newInstance
//    targetInstance.asInstanceOf[ProxyObject].setHandler(interceptor)
//    targetInstance.asInstanceOf[T]

    factory.create(args.map(_.getClass), args, interceptor).asInstanceOf[T]
  }
}