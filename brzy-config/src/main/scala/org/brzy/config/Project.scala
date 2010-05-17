package org.brzy.config

import reflect.BeanProperty

/**
 * @author Michael Fortin
 * @version $Id : $
 */
class Project(m: Map[String, AnyRef]) extends Config(m) with MergeConfig[Project] {

  val scalaVersion: String = set[String](m.get("scala_version"))
  val antVersion: String = set[String](m.get("ant_version"))
  val ivyVersion: String = set[String](m.get("ivy_version"))
  val packageType: String = set[String](m.get("package_type"))
  val pluginResources: String = set[String](m.get("plugin_resources"))
  val pluginRepository: String = set[String](m.get("plugin_repository"))

  val configurationName = "Project"

  def asMap = {
    Map[String, AnyRef](
      "scala_version" -> scalaVersion,
      "ant_version" -> antVersion,
      "ivy_version" -> ivyVersion,
      "package_type" -> packageType,
      "plugin_resources" -> pluginResources,
      "plugin_repository" -> pluginRepository)
  }

  def +(that: Project) = {
    if(that != null)
      new Project(this.asMap ++ that.asMap)
    else
      new Project(this.asMap)
  }
}