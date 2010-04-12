package org.brzy.build

import org.brzy.config.Config
import io.Source

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class BuildProperties(config:Config) {

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
      .replace(applicationVersion,config.version)
      .replace(applicationName,config.name)
      .replace(applicationOrg,config.group_id)
      .replace(applicationType,config.package_type)
      .replace(testFramework,config.test_framework)
      .replace(scalaVersion,config.scala_version)
      .replace(ivyVersion,config.ivy_version)
}