package org.brzy.config

import reflect.BeanProperty

/**
 * @author Michael Fortin
 * @version $Id : $
 */
class Project(m: Map[String, AnyRef]) extends Config(m) with MergeConfig[Project] {
  val configurationName: String = "Project"
  val scalaVersion: Option[String] = m.get("scala_version").asInstanceOf[Option[String]].orElse(None)
  val antVersion: Option[String] = m.get("ant_version").asInstanceOf[Option[String]].orElse(None)
  val ivyVersion: Option[String] = m.get("ivy_version").asInstanceOf[Option[String]].orElse(None)
  val packageType: Option[String] = m.get("package_type").asInstanceOf[Option[String]].orElse(None)
  val pluginResources: Option[String] = m.get("plugin_resources").asInstanceOf[Option[String]].orElse(None)
  val pluginRepository: Option[String] = m.get("plugin_repository").asInstanceOf[Option[String]].orElse(None)


  def asMap = {
    Map[String, AnyRef](
      "scala_version" -> scalaVersion,
      "ant_version" -> antVersion,
      "ivy_version" -> ivyVersion,
      "package_type" -> packageType,
      "plugin_resources" -> pluginResources,
      "plugin_repository" -> pluginRepository)
  }

  def <<(that: Project) = {
    if (that == null) {
      this
    }
    else {
      new Project(Map[String,String](
        "scala_version" -> that.scalaVersion.getOrElse(this.scalaVersion.get),
        "ant_version" -> that.antVersion.getOrElse(this.antVersion.get),
        "ivy_version" -> that.ivyVersion.getOrElse(this.ivyVersion.get),
        "package_type" -> that.packageType.getOrElse(this.packageType.get),
        "plugin_resources" -> that.pluginResources.getOrElse(this.pluginResources.get),
        "plugin_repository" -> that.pluginRepository.getOrElse(this.pluginRepository.get)
        ))
    }
  }
}