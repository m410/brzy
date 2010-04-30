package org.brzy.build

import org.brzy.config.AppConfig
import io.Source

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class BuildProperties(config:AppConfig) {

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
      .replace(applicationOrg,config.application.group_id)
      .replace(applicationType,config.project.package_type)
      .replace(testFramework,config.test_framework)
      .replace(scalaVersion,config.project.scala_version)
      .replace(ivyVersion,config.project.ivy_version)
}