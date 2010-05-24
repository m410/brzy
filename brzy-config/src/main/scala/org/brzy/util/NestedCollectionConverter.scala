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
      case j: JMap[String,AnyRef] => smap.put(nvp._1, convertMap(j))
      case j: JList[AnyRef] => smap.put(nvp._1, convertList(j))
      case _ => smap.put(nvp._1, nvp._2)
    })
    smap.toMap
  }

  def convertList(list: JList[AnyRef]): List[AnyRef] = {
    val slist = ListBuffer[AnyRef]()
    list.foreach(i => i match {
      case j: JMap[String,AnyRef] => slist += convertMap(j)
      case j: JList[AnyRef] => slist += convertList(j)
      case _ => slist += i

    })
    slist.toList
  }
}