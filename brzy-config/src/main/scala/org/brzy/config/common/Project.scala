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

import reflect.BeanProperty

/**
 * Project is one of the top level configurations.  It generally is not overridden in
 * the brzy-webapp.b.yml but can be.  For example if you want to use a different version
 * of scala other than the default.
 * 
 * @author Michael Fortin
 */
class Project(m: Map[String, AnyRef]) extends Config(m) with MergeConfig[Project] {
  val configurationName: String = "Project"
  val scalaVersion: Option[String] = m.get("scala_version").asInstanceOf[Option[String]].orElse(null)
  val sbtVersion: Option[String] = m.get("sbt_version").asInstanceOf[Option[String]].orElse(null)
  val packageType: Option[String] = m.get("package_type").asInstanceOf[Option[String]].orElse(null)

  val moduleResources: Option[String] = m.get("module_resources").asInstanceOf[Option[String]].orElse(null)
  val moduleRepository: Option[String] = m.get("module_repository").asInstanceOf[Option[String]].orElse(null)


  override def asMap:Map[String,AnyRef] = m

  def <<(that: Project) = {
    if (that == null) {
      this
    }
    else {
      new Project(Map[String,String](
        "scala_version" -> that.scalaVersion.getOrElse(this.scalaVersion.getOrElse(null)),
        "sbt_version" -> that.sbtVersion.getOrElse(this.sbtVersion.getOrElse(null)),
        "package_type" -> that.packageType.getOrElse(this.packageType.getOrElse(null)),
        "module_resources" -> that.moduleResources.getOrElse(this.moduleResources.getOrElse(null)),
        "module_repository" -> that.moduleRepository.getOrElse(this.moduleRepository.getOrElse(null))
        ))
    }
  }
}