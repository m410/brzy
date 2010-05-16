package org.brzy.config

import collection.JavaConversions._
import java.lang.reflect.Constructor
import java.lang.String
import collection.immutable.Map
import java.util.{List => JList, Map => JMap}

/**
 *
 * @author Michael Fortin
 * @version $Id : $
 */
abstract class Config(map: Map[String, AnyRef]) {
  def this(jmap: JMap[String, AnyRef]) = this (jmap.toMap)

  def this() = this (Map[String, AnyRef]())

  val configurationName: String

  def asMap: Map[String, AnyRef]


  protected def set[T](s: Option[Any]):T = s match {
    case s: Some[T] => s.get
    case _ => null.asInstanceOf[T]
  }

  protected def make[T](c: Class[T], t: Option[Any]):T = t match {
    case s: Some[JMap[_, _]] => c.getConstructor(classOf[Map[String, AnyRef]]).newInstance(s.get.toMap)
    case _ => null.asInstanceOf[T]
  }

  protected def makeSeq[T](c: Class[T], t: Option[Any]): Seq[T] = t match {
    case s: Some[JList[AnyRef]] => s.get.map(i => {
      val constructor = c.getConstructor(classOf[Map[String, AnyRef]])
      val toMap: Map[String, AnyRef] = i.asInstanceOf[JMap[String, AnyRef]].toMap
      constructor.newInstance(toMap)
    })
    case _ => Nil
  }

  protected def makeWebXml(list: Option[Any]) = list match {
    case s: Some[JList[JMap[_, _]]] => s.get.map(depMap => depMap.toMap)
    case _ => null
  }

  protected def makePlugin(m: Option[Any]) = m match {
    case s: Some[JMap[String, AnyRef]] =>
      val clazz = Class.forName(s.get.get("config_class").asInstanceOf[String])
      clazz.getConstructor(classOf[Map[String, AnyRef]]).newInstance(s.get.toMap)
    case _ => null
  }

  protected def makePluginList(m: Option[Any]) = m match {
    case s: Some[JList[AnyRef]] =>
      s.get.map(map => {
        val jmap: JMap[String, String] = map.asInstanceOf[JMap[String, String]]
        val className = jmap.get("config_class")
        val clazz = Class.forName(className)
        clazz.getConstructor(classOf[Map[String, AnyRef]]).newInstance(jmap.toMap)
      })
    case _ => null
  }
}