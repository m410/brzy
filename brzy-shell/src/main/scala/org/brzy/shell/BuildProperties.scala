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
package org.brzy.shell

import io.Source
import org.brzy.config.common.BootConfig

/**
 * @author Michael Fortin
 */
class BuildProperties(config:BootConfig) {

  private val applicationVersion="[version]"
  private val applicationName="[name]"
  private val applicationOrg="[package]"
  private val applicationType="[apptype]"
  private val scalaVersion="[scalaversion]"
  private val sbtVersion="[sbtversion]"

  // read template
  val content = Source.fromURL(getClass.getClassLoader.getResource("template.build.properties"))
      .mkString
      .replace(applicationVersion,config.application.get.version.get)
      .replace(applicationName,config.application.get.name.get)
      .replace(applicationOrg,config.application.get.org.get)
      .replace(applicationType,config.project.get.packageType.get)
      .replace(scalaVersion,config.project.get.scalaVersion.get)
      .replace(sbtVersion,config.project.get.sbtVersion.get)
}