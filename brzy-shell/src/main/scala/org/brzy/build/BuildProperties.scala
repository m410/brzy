package org.brzy.build

import org.brzy.config.WebappConfig
import io.Source

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class BuildProperties(config:WebappConfig) {

  private val applicationVersion="[version]"
  private val applicationName="[name]"
  private val applicationOrg="[package]"
  private val applicationType="[apptype]"
  private val testFramework="[testframework]"
  private val scalaVersion="[scalaversion]"
  private val ivyVersion="[ivyversion]"

  // read template
  val content = Source.fromURL(getClass.getClassLoader.getResource("template.build.properties"))
      .mkString
      .replace(applicationVersion,config.application.get.version.get)
      .replace(applicationName,config.application.get.name.get)
      .replace(applicationOrg,config.application.get.org.get)
      .replace(applicationType,config.project.get.packageType.get)
      .replace(testFramework,config.testFramework.get)
      .replace(scalaVersion,config.project.get.scalaVersion.get)
      .replace(ivyVersion,config.project.get.ivyVersion.get)
}