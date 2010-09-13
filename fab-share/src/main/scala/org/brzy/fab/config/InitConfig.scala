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
class InitConfig(val map: Map[String, AnyRef]){

  val views: Option[Mod] = map.get("views") match {
    case Some(s) =>
      if (s != null)
        Option(new Mod(s.asInstanceOf[Map[String, AnyRef]]))
      else
        None
    case _ => None
  }

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

  val dependencyExcludes: Option[List[Dependency]] = map.get("dependency_excludes") match {
    case Some(s) =>
      if (s != null)
        Option(s.asInstanceOf[List[Map[String, AnyRef]]].map(i => new Dependency(i)).toList)
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
  def <<(that: InitConfig) = {

    if (that == null) {
      this
    }
    else {
      new InitConfig(Map[String, AnyRef](
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
        },
        "views" -> {
          if (this.views.isDefined && this.views.get != null)
            {this.views.get << that.views.getOrElse(null)}.map
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
        }
        ))
    }
  }
}