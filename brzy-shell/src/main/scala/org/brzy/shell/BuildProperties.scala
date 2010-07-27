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