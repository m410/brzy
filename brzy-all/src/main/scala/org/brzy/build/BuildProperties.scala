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
  val content = Source.fromURL(ClassLoader.getSystemResource("template.build.properties"))
      .mkString
      .replaceAll(applicationVersion,config.version)
      .replaceAll(applicationName,config.name)
      .replaceAll(applicationOrg,config.group_id)
      .replaceAll(applicationType,config.package_type)
      .replaceAll(testFramework,config.test_framework)
      .replaceAll(scalaVersion,config.scala_version)
      .replaceAll(ivyVersion,config.ivy_version)
}