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


import collection.mutable.ListBuffer


/**
 *
 * @author Michael Fortin
 */
class BaseConfig(val map: Map[String, AnyRef]){


  val repositories: Option[List[Repository]] = map.get("repositories") match {
    case Some(s) =>
      if (s != null)
        Option(s.asInstanceOf[List[Map[String, AnyRef]]].map(i => new Repository(i)).toList)
      else
        None
    case _ => None
  }

  val dependencies: Option[List[Dependency]] = map.get("dependencies") match {
    case Some(s) =>
      if (s != null)
        Option(s.asInstanceOf[List[Map[String, AnyRef]]].map(i => new Dependency(i)).toList)
      else
        None
    case _ => None
  }


  /**
   * merge this with other config, and return a new one
   */
  def <<(that: BaseConfig) = {

    if (that == null) {
      this
    }
    else {
      new BaseConfig(Map[String, AnyRef](
        "repositories" -> {
          if (this.repositories.isDefined && that.repositories.isDefined)
            this.repositories.get.map(_.map).toList ++ that.repositories.get.map(_.map).toList
          else if (this.repositories.isDefined)
            this.repositories.get.map(_.map).toList
          else if (that.repositories.isDefined)
            that.repositories.get.map(_.map).toList
          else
            null
        },
        "dependencies" -> {
          if (this.dependencies.isDefined && that.dependencies.isDefined)
            this.dependencies.get.map(_.map).toList ++ that.dependencies.get.map(_.map).toList
          else if (this.dependencies.isDefined)
            this.dependencies.get.map(_.map).toList
          else if (that.dependencies.isDefined)
            that.dependencies.get.map(_.map).toList
          else
            null
        }
        ))
    }
  }
}