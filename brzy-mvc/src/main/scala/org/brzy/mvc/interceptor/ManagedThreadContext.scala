package org.brzy.mvc.interceptor

import util.DynamicVariable

/**
 * This is implemented by modules to add thread scope variable management to a
 * web application.
 * 
 * @author Michael Fortin
 */
trait ManagedThreadContext {
  type T <: AnyRef

  val packageScope:String = ""

  val context: DynamicVariable[T]

  val empty: T

  def createSession:T

  def destroySession(target:T)

  def isManaged(target: AnyRef) = {
    if(packageScope != "") {
      if(target.getClass.getPackage.getName.startsWith(packageScope))
         true
      else
        false
    }
    else {
      true
    }
  }
}