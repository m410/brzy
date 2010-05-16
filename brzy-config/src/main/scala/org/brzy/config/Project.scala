package org.brzy.config

import reflect.BeanProperty

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class Project(m:Map[String,AnyRef]) extends Config(m) with MergeConfig[Project]{

  val scalaVersion:String = set[String](m.get("scala_version"))
  val antVersion:String = set[String](m.get("ant_version"))
  val ivyVersion:String = set[String](m.get("ivy_version"))
  val packageType:String = set[String](m.get("package_type"))
  val pluginResources:String = set[String](m.get("plugin_resources"))
  val pluginRepository:String = set[String](m.get("plugin_repository"))

  val configurationName = "Project"

  def asMap = {
    val map = Map[String,AnyRef]()
    // TODO add each property
    map
  }

  def +(that: Project) =  new Project(this.asMap ++ that.asMap)
}