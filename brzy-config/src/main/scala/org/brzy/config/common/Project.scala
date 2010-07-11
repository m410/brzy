package org.brzy.config.common

import reflect.BeanProperty

/**
 * @author Michael Fortin
 */
class Project(m: Map[String, AnyRef]) extends Config(m) with MergeConfig[Project] {
  val configurationName: String = "Project"
  val scalaVersion: Option[String] = m.get("scala_version").asInstanceOf[Option[String]].orElse(null)
  val sbtVersion: Option[String] = m.get("sbt_version").asInstanceOf[Option[String]].orElse(null)
  val packageType: Option[String] = m.get("package_type").asInstanceOf[Option[String]].orElse(null)

  val moduleResources: Option[String] = m.get("module_resources").asInstanceOf[Option[String]].orElse(null)
  val moduleRepository: Option[String] = m.get("module_repository").asInstanceOf[Option[String]].orElse(null)


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
        "module_resources" -> that.moduleResources.getOrElse(this.moduleResources.getOrElse(null)),
        "module_repository" -> that.moduleRepository.getOrElse(this.moduleRepository.getOrElse(null))
        ))
    }
  }
}