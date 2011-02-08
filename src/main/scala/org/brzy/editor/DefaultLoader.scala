package org.brzy.editor

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
class DefaultLoader[T] extends Loader[T]{

  def init(argTypes: Array[Class[_]], args: Array[AnyRef])(implicit m:Manifest[T]) = {
    val const = m.erasure.getConstructor(argTypes.toArray:_*)
    const.newInstance(args.toArray:_*).asInstanceOf[T]
  }

  def init(properties:Map[String,String])(implicit m:Manifest[T]) = {
    val const = m.erasure.getConstructor()
    const.newInstance().asInstanceOf[T]
  }
}