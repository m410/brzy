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
package org.brzy.fab.cli.mod

import collection.mutable.ListBuffer

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
class ModConf(override val map: Map[String, AnyRef]) extends BaseConf(map){
  val views: Option[Mod] = map.get("views") match {
    case Some(s) =>
      if (s != null)
        Option(new Mod(s.asInstanceOf[Map[String, AnyRef]]))
      else
        None
    case _ => None
  }

  val modules: Option[List[Mod]] = map.get("modules") match {
    case Some(s) =>
      if (s != null) {
        val buffer = new ListBuffer[Mod]()
        s.asInstanceOf[List[Map[String, AnyRef]]].foreach(map => {
          buffer += new Mod(map)
        })
        Option(buffer.toList)
      }
      else
        None
    case _ => None
  }

  val persistence: Option[List[Mod]] = map.get("persistence") match {
    case Some(s) =>
      val buffer = new ListBuffer[Mod]()
      if (s != null) {
        s.asInstanceOf[List[Map[String, AnyRef]]].foreach(map => {
          buffer += new Mod(map)
        })
        Option(buffer.toList)
      }
      else {
        None
      }
    case _ => None
  }

  /**
   * merge this with other config, and return a new one
   */
  override def <<(it: BaseConf) = {

    if (it == null) {
      this
    }
    else {
      val that = it.asInstanceOf[ModConf]
      new ModConf(Map[String, AnyRef](
        "views" -> {
          if (this.views.isDefined && this.views.get != null)
            {this.views.get << that.views.orNull}.map
          else if (that.views.isDefined)
            that.views.get.map
          else
            null
        },
        "persistence" -> {
          if (this.persistence.isDefined && that.persistence.isDefined)
            {this.persistence.get ++ that.persistence.get}.distinct.map(_.map).toList
          else if (this.persistence.isDefined)
            this.persistence.get.distinct.map(_.map).toList
          else if (that.persistence.isDefined)
            that.persistence.get.distinct.map(_.map).toList
          else
            null
        },
        "modules" -> {
          if (this.modules.isDefined && that.modules.isDefined)
            {this.modules.get ++ that.modules.get}.distinct.map(_.map).toList
          else if (this.modules.isDefined)
            this.modules.get.distinct.map(_.map).toList
          else if (that.modules.isDefined)
            that.modules.get.distinct.map(_.map).toList
          else
            null
      }) ++ super.<<(that).map)
    }
  }
}