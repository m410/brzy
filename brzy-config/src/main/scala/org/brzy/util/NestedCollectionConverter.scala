package org.brzy.util


import collection.JavaConversions._
import java.util.{Map => JMap, List => JList}
import collection.mutable.{ListBuffer, HashMap}

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */

object NestedCollectionConverter {

  def convertMap(map: JMap[String, AnyRef]): Map[String, AnyRef] = {
    val smap = HashMap[String, AnyRef]()
    map.foreach(nvp => nvp._2 match {
      case j: JMap[_,_] => smap.put(nvp._1.asInstanceOf[String], convertMap(j.asInstanceOf[JMap[String,AnyRef]]))
      case j: JList[_] => smap.put(nvp._1.asInstanceOf[String], convertList(j.asInstanceOf[JList[AnyRef]]))
      case _ => smap.put(nvp._1, nvp._2)
    })
    smap.toMap
  }

  def convertList(list: JList[AnyRef]): List[AnyRef] = {
    val slist = ListBuffer[AnyRef]()
    list.foreach(i => i match {
      case j: JMap[_,_] => slist += convertMap(j.asInstanceOf[JMap[String,AnyRef]])
      case j: JList[_] => slist += convertList(j.asInstanceOf[JList[AnyRef]])
      case _ => slist += i

    })
    slist.toList
  }
}