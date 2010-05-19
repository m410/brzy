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
      .replace(applicationVersion,config.application.version)
      .replace(applicationName,config.application.name)
      .replace(applicationOrg,config.application.org)
      .replace(applicationType,config.project.packageType)
      .replace(testFramework,config.testFramework)
      .replace(scalaVersion,config.project.scalaVersion)
      .replace(ivyVersion,config.project.ivyVersion)
}