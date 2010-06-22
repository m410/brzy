package org.brzy.config.common

import reflect.BeanProperty

/**
 * @author Michael Fortin
 * @version $Id : $
 */
class Project(m: Map[String, AnyRef]) extends Config(m) with MergeConfig[Project] {
  val configurationName: String = "Project"
  val scalaVersion: Option[String] = m.get("scala_version").asInstanceOf[Option[String]].orElse(null)
  val sbtVersion: Option[String] = m.get("sbt_version").asInstanceOf[Option[String]].orElse(null)
  val packageType: Option[String] = m.get("package_type").asInstanceOf[Option[String]].orElse(null)

  val pluginResources: Option[String] = m.get("plugin_resources").asInstanceOf[Option[String]].orElse(null)
  val pluginRepository: Option[String] = m.get("plugin_repository").asInstanceOf[Option[String]].orElse(null)


  override def asMap:Map[String,AnyRef] = m

  def <<(that: Project) = {
    if (that == null) {
      this
    }
    else {
      new Project(Map[String,String](
        "scala_version" -> that.scalaVersion.getOrElse(this.scalaVersion.getOrElse(null)),
        "sbt_version" -> that.sbtVersion.getOrElse(this.sbtVersion.getOrElse(null)),
        "package_type" -> that.packageType.getOrElse(this.packageType.getOrElse(null)),
        "plugin_resources" -> that.pluginResources.getOrElse(this.pluginResources.getOrElse(null)),
        "plugin_repository" -> that.pluginRepository.getOrElse(this.pluginRepository.getOrElse(null))
        ))
    }
  }
}