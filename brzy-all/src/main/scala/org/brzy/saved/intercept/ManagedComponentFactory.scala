package org.brzy.saved.intercept

import java.lang.reflect.{Method, InvocationHandler, Proxy}

/**
 * http://java.dzone.com/articles/real-world-scala-managing-cros
 *
 * @author Michael Fortin
 * @version $Id: $
 */
object ManagedComponentFactory {

  def createComponent[T](intf: Class[T], proxy: ManagedComponentProxy): T =
    Proxy.newProxyInstance(proxy.target.getClass.getClassLoader,Array(intf),proxy).asInstanceOf[T]
}