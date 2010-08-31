/*
 * Copyright 2010 Michael Fortin <mike@brzy.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.brzy.util

import collection.JavaConversions._
import java.util.{Map => JMap, List => JList}
import collection.mutable.{ListBuffer, HashMap}

/**
 * This is used to convert the java collection hierarchy created by the jyml library
 * into scala collection classes.
 * 
 * @author Michael Fortin
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