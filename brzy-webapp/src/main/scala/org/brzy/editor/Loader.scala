package org.brzy.editor

/**
 * Loads an instance of a bean from a data store.
 * 
 * @author Michael Fortin
 */
trait Loader[T] {
  def init(argTypes:Array[Class[_]],args:Array[AnyRef])(implicit m:Manifest[T]):T
  def init(properties:Map[String,String])(implicit m:Manifest[T]):T
}