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
package org.brzy.fab.config


import java.io.File

import collection.mutable.{ListBuffer, HashMap}
import collection.JavaConversions._
import org.ho.yaml.Yaml
import java.util.{Map => JMap, List => JList}

/**
 * Loads a configuration file into a InitConfig object.
 * 
 * @author Michael Fortin
 */
object LoadInitConfig {

  def apply(file:File) = {
    val mp = convertMap(Yaml.load(file).asInstanceOf[JMap[String, AnyRef]])
    new InitConfig(mp)
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