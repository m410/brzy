package org.brzy.editor

import collection.mutable.ListBuffer

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
case class Bean[T: Manifest](editors: List[Editor[_]], loader: Loader[T] = new DefaultLoader[T]) {
  val m = manifest[T]
  val constructorProperties = editors.filter(_.isConstructor).sortBy(_.index)
  val fieldProperties = editors.filter(!_.isConstructor)

  def make(properties: Map[String, String]): T = {
    val argTypes = ListBuffer[Class[_]]()
    val args = ListBuffer[AnyRef]()
    constructorProperties.foreach(e => {
      argTypes += e.editorClass
      args += e.fromText(properties(e.property)).asInstanceOf[AnyRef]
    })
    val inst = if (constructorProperties.isEmpty)
      loader.init(properties)
    else
      loader.init(argTypes.toArray, args.toArray)

    fieldProperties.foreach(p => {
      val method = m.erasure.getMethod(p.property + "_$eq", p.editorClass)

      if (properties.get(p.property).isDefined)
        method.invoke(inst, p.fromText(properties(p.property)).asInstanceOf[AnyRef])
    })

    inst
  }
}