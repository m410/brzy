package org.brzy.config

import collection.JavaConversions._
import java.lang.reflect.Constructor
import java.lang.String
import collection.immutable.Map
import java.util.{List => JList, Map => JMap}
import org.brzy.plugin.Plugin

/**
 *
 * @author Michael Fortin
 * @version $Id : $
 */
abstract class Config(map: Map[String, AnyRef]) {

  def this() = this (Map[String, AnyRef]())

  val configurationName: String

  def asMap: Map[String, AnyRef]

  protected def set[T](s: Option[Any]): T = s match {
    case s: Some[T] => s.get
    case _ => null.asInstanceOf[T]
  }

  protected def make[T](c: Class[T], t: Option[Any]): T =
    t match {
      case s: Some[Map[_, _]] =>
        if (s.get.isInstanceOf[java.util.HashMap[_, _]]) {
          val hm = s.get.asInstanceOf[java.util.HashMap[_, _]].toMap
          c.getConstructor(classOf[Map[String, AnyRef]]).newInstance(hm)
        }
        else
          c.getConstructor(classOf[Map[String, AnyRef]]).
                  newInstance(s.get)
      case _ =>
        null.asInstanceOf[T]
    }

  protected def makeSeq[T](c: Class[T], t: Option[Any]): Seq[T] = t match {
    case s: Some[List[_]] =>
      if (s.get.isInstanceOf[java.util.List[_]]) {
        val list = s.get.asInstanceOf[java.util.List[_]]
        list.map(i => {
          val constructor = c.getConstructor(classOf[Map[String, AnyRef]])
          val toMap: Map[String, AnyRef] = i.asInstanceOf[JMap[String, AnyRef]].toMap
          constructor.newInstance(toMap)
        })
      }
      else {
        val list = s.get.asInstanceOf[collection.mutable.ArrayBuffer[_]]
        s.get.map(i => {
          val constructor = c.getConstructor(classOf[Map[String, AnyRef]])
          val toMap: Map[String, AnyRef] = i.asInstanceOf[Map[String, AnyRef]]
          constructor.newInstance(toMap)
        })
      }
    case _ => null
  }

  protected def makeWebXml(list: Option[Any]) = list match {
    case s: Some[JList[JMap[_, _]]] =>
      if(s.get == null)
        null
      else if(s.get.isInstanceOf[JList[JMap[_, _]]]) {
        s.get.asInstanceOf[JList[JMap[String, AnyRef]]].map(depMap => depMap.toMap)
      }
      else {
        val buffer = s.get.asInstanceOf[collection.mutable.ArrayBuffer[Map[String, AnyRef]]]
        buffer.map(depMap =>
          depMap.toMap)
      }
    case _ => null
  }

  protected def makePlugin(m: Option[Any]):Plugin[_] = m match {
    case s: Some[Map[String, AnyRef]] =>
      if (s.get.isInstanceOf[java.util.HashMap[_, _]]) {
        val hm = s.get.asInstanceOf[java.util.HashMap[String, AnyRef]].toMap
        val clazz = Class.forName(hm.get("config_class").get.asInstanceOf[String])
        clazz.getConstructor(classOf[Map[String, AnyRef]]).newInstance(hm).asInstanceOf[Plugin[_]]
      }
      else {
        val clazz = Class.forName(s.get.get("config_class").get.asInstanceOf[String])
        clazz.getConstructor(classOf[Map[String, AnyRef]]).newInstance(s.get).asInstanceOf[Plugin[_]]
      }

    case _ => null
  }

  protected def makePluginList(m: Option[Any]) = m match {
    case s: Some[List[AnyRef]] =>
      if (s.get.isInstanceOf[java.util.List[_]]) {
        s.get.asInstanceOf[java.util.List[_]].map(map => {
          val jmap: JMap[String, String] = map.asInstanceOf[JMap[String, String]]
          val className = jmap.get("config_class")
          val clazz = Class.forName(className)
          clazz.getConstructor(classOf[Map[String, AnyRef]]).newInstance(jmap.toMap)
        })
      }
      else {
        s.get.asInstanceOf[collection.mutable.ArrayBuffer[_]].map(map => {
          val jmap: Map[String, String] = map.asInstanceOf[Map[String, String]]
          val className = jmap.get("config_class").get
          val clazz = Class.forName(className)
          clazz.getConstructor(classOf[Map[String, AnyRef]]).newInstance(jmap)
        })
      }
    case _ => null
  }
}