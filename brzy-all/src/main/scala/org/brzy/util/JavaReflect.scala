package org.brzy.util

import collection.mutable.ListBuffer
import scala.collection.mutable.WrappedArray._
import org.slf4j.{Logger, LoggerFactory}

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
object JavaReflect {

  def annotations(inst:AnyRef) = {
    inst.getClass.getDeclaredAnnotations.toList
  }

  def methods(inst:AnyRef) = {
    Array(inst.getClass.getDeclaredMethods)
  }

  def methodReturn(func:AnyRef) = {
    val clazz = func.getClass
    Array.empty[AnyRef]
  }

  def methodArguments(func:AnyRef) = {
    val clazz = func.getClass
    Array.empty[AnyRef]
  }
}