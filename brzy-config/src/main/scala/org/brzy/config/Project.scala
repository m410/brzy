package org.brzy.config

import reflect.BeanProperty

/**
 * @author Michael Fortin
 * @version $Id : $
 */
class Project(m: Map[String, AnyRef]) extends Config(m) with MergeConfig[Project] {
  val configurationName: String = "Project"
  val scalaVersion: Option[String] = m.get("scala_version").asInstanceOf[Option[String]].orElse(null)
  val antVersion: Option[String] = m.get("ant_version").asInstanceOf[Option[String]].orElse(null)
  val ivyVersion: Option[String] = m.get("ivy_version").asInstanceOf[Option[String]].orElse(null)
  val packageType: Option[String] = m.get("package_type").asInstanceOf[Option[String]].orElse(null)
  val pluginResources: Option[String] = m.get("plugin_resources").asInstanceOf[Option[String]].orElse(null)
  val pluginRepository: Option[String] = m.get("plugin_repository").asInstanceOf[Option[String]].orElse(null)


  def asMap = {
    Map[String, AnyRef](
      "scala_version" -> scalaVersion.getOrElse(null),
      "ant_version" -> antVersion.getOrElse(null),
      "ivy_version" -> ivyVersion.getOrElse(null),
      "package_type" -> packageType.getOrElse(null),
      "plugin_resources" -> pluginResources.getOrElse(null),
      "plugin_repository" -> pluginRepository.getOrElse(null))
  }

  def <<(that: Project) = {
    if (that == null) {
      this
    }
    else {
      new Project(Map[String,String](
        "scala_version" -> that.scalaVersion.getOrElse(this.scalaVersion.getOrElse(null)),
        "ant_version" -> that.antVersion.getOrElse(this.antVersion.getOrElse(null)),
        "ivy_version" -> that.ivyVersion.getOrElse(this.ivyVersion.getOrElse(null)),
        "package_type" -> that.packageType.getOrElse(this.packageType.getOrElse(null)),
        "plugin_resources" -> that.pluginResources.getOrElse(this.pluginResources.getOrElse(null)),
        "plugin_repository" -> that.pluginRepository.getOrElse(this.pluginRepository.getOrElse(null))
        ))
    }
  }
}