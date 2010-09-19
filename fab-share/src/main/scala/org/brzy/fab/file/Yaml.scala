package org.brzy.fab.file

import java.io.{File=>JFile}

import collection.mutable.{ListBuffer, HashMap}
import collection.JavaConversions._
import org.ho.yaml.{Yaml=>JYaml}
import java.util.{Map => JMap, List => JList}

/**
 * Opens a YAML file and returns a data structure consisting of scala lists and maps.
 * 
 * @author Michael Fortin
 */
object Yaml {
  def apply(file:JFile) = {
    convertMap(JYaml.load(file).asInstanceOf[JMap[String, AnyRef]])
  }

  protected[this]  def convertMap(map: JMap[String, AnyRef]): Map[String, AnyRef] = {
    val smap = HashMap[String, AnyRef]()
    map.foreach(nvp => nvp._2 match {
      case j: JMap[_,_] => smap.put(nvp._1.asInstanceOf[String], convertMap(j.asInstanceOf[JMap[String,AnyRef]]))
      case j: JList[_] => smap.put(nvp._1.asInstanceOf[String], convertList(j.asInstanceOf[JList[AnyRef]]))
      case _ => smap.put(nvp._1, nvp._2)
    })
    smap.toMap
  }

  protected[this] def convertList(list: JList[AnyRef]): List[AnyRef] = {
    val slist = ListBuffer[AnyRef]()
    list.foreach(i => i match {
      case j: JMap[_,_] => slist += convertMap(j.asInstanceOf[JMap[String,AnyRef]])
      case j: JList[_] => slist += convertList(j.asInstanceOf[JList[AnyRef]])
      case _ => slist += i

    })
    slist.toList
  }
}