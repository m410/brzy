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
package org.brzy.config.common

import org.brzy.config.mod.Mod
import java.lang.String
import collection.mutable.ListBuffer

/**
 * This is the initial configuration setup in the brzy-webapp.b.yml file.  This
 * gets created before the modules can be loaded and added to the application
 * configuration.
 *
 * @author Michael Fortin
 */
class BootConfig(m: Map[String, AnyRef]) extends Config(m) with MergeConfig[BootConfig] {
  private val dev = "developement"
  private val prod = "production"
  private val test = "test"
  val configurationName: String = "Boot Configuration"
  val environment: Option[String] = m.get("environment").asInstanceOf[Option[String]].orElse(None)
  val application: Option[Application] = m.get("application") match {
    case s: Some[_] => Option(new Application(s.get.asInstanceOf[Map[String, String]]))
    case _ => None
  }
  val project: Option[Project] = m.get("project") match {
    case Some(s) =>
      if (s != null)
        Option(new Project(s.asInstanceOf[Map[String, String]]))
      else
        None
    case _ => None
  }
  val logging: Option[Logging] = m.get("logging") match {
    case Some(s) =>
      if (s != null)
        Option(new Logging(s.asInstanceOf[Map[String, AnyRef]]))
      else
        None
    case _ => None
  }
  val webXml: Option[List[Map[String, AnyRef]]] = m.get("web_xml") match {
    case Some(s) =>
      if (s != null && s.isInstanceOf[List[_]])
        Option(s.asInstanceOf[List[Map[String, AnyRef]]])
      else
        None
    case _ => None
  }

  val views: Option[Mod] = m.get("views") match {
    case Some(s) =>
      if (s != null)
        Option(new Mod(s.asInstanceOf[Map[String, AnyRef]]))
      else
        None
    case _ => None
  }

  val repositories: Option[List[Repository]] = m.get("repositories") match {
    case Some(s) =>
      if (s != null)
        Option(s.asInstanceOf[List[Map[String, AnyRef]]].map(i => new Repository(i)).toList)
      else
        None
    case _ => None
  }
  val dependencies: Option[List[Dependency]] = m.get("dependencies") match {
    case Some(s) =>
      if (s != null)
        Option(s.asInstanceOf[List[Map[String, AnyRef]]].map(i => new Dependency(i)).toList)
      else
        None
    case _ => None
  }

  val dependencyExcludes: Option[List[Dependency]] = m.get("dependency_excludes") match {
    case Some(s) =>
      if (s != null)
        Option(s.asInstanceOf[List[Map[String, AnyRef]]].map(i => new Dependency(i)).toList)
      else
        None
    case _ => None
  }
  
  val modules: Option[List[Mod]] = m.get("modules") match {
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
  val persistence: Option[List[Mod]] = m.get("persistence") match {
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

  def asMap = m

  /**
   * merge this with other config, and return a new one
   */
  def <<(that: BootConfig) = {

    if (that == null) {
      this
    }
    else {
      new BootConfig(Map[String, AnyRef](
        "environment" -> this.environment.getOrElse(that.environment.get),
        "application" -> {this.application.getOrElse(that.application.get)}.asMap,
        "project" -> {
          if (this.project.isDefined && this.project.get != null)
            {this.project.get << that.project.getOrElse(null)}.asMap
          else if (that.project.isDefined && that.project.get != null)
            that.project.get.asMap
          else
            null
        },
        "repositories" -> {
          if (this.repositories.isDefined && that.repositories.isDefined)
            this.repositories.get.map(_.asMap).toList ++ that.repositories.get.map(_.asMap).toList
          else if (this.repositories.isDefined)
            this.repositories.get.map(_.asMap).toList
          else if (that.repositories.isDefined)
            that.repositories.get.map(_.asMap).toList
          else
            null
        },
        "dependencies" -> {
          if (this.dependencies.isDefined && that.dependencies.isDefined)
            this.dependencies.get.map(_.asMap).toList ++ that.dependencies.get.map(_.asMap).toList
          else if (this.dependencies.isDefined)
            this.dependencies.get.map(_.asMap).toList
          else if (that.dependencies.isDefined)
            that.dependencies.get.map(_.asMap).toList
          else
            null
        },
        "dependency_excludes" -> {
          if (this.dependencyExcludes.isDefined && that.dependencyExcludes.isDefined)
            this.dependencyExcludes.get.map(_.asMap).toList ++ that.dependencyExcludes.get.map(_.asMap).toList
          else if (this.dependencyExcludes.isDefined)
            this.dependencyExcludes.get.map(_.asMap).toList
          else if (that.dependencyExcludes.isDefined)
            that.dependencyExcludes.get.map(_.asMap).toList
          else
            null
        },
        "views" -> {
          if (this.views.isDefined && this.views.get != null)
            {this.views.get << that.views.getOrElse(null)}.asMap
          else if (that.views.isDefined)
            that.views.get.asMap
          else
            null
        },
        "persistence" -> {
          if (this.persistence.isDefined && that.persistence.isDefined)
            {this.persistence.get ++ that.persistence.get}.distinct.map(_.asMap).toList
          else if (this.persistence.isDefined)
            this.persistence.get.distinct.map(_.asMap).toList
          else if (that.persistence.isDefined)
            that.persistence.get.distinct.map(_.asMap).toList
          else
            null
        },
        "modules" -> {
          if (this.modules.isDefined && that.modules.isDefined)
            {this.modules.get ++ that.modules.get}.distinct.map(_.asMap).toList
          else if (this.modules.isDefined)
            this.modules.get.distinct.map(_.asMap).toList
          else if (that.modules.isDefined)
            that.modules.get.distinct.map(_.asMap).toList
          else
            null
        },
        "web_xml" -> {
          if (this.webXml.isDefined && that.webXml.isDefined)
            this.webXml.get ++ that.webXml.get
          else if (this.webXml.isDefined)
            this.webXml.get
          else if (that.webXml.isDefined)
            that.webXml.get
          else
            None
        },
        "logging" -> {
          if (this.logging.isDefined)
            {this.logging.get << that.logging.getOrElse(null)}.asMap
          else if (that.logging.isDefined)
            that.logging.get.asMap
          else
            None
        }
        ))
    }
  }
}