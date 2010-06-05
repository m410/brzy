package org.brzy.config.webapp

import org.brzy.config.plugin.Plugin
import org.brzy.config.common._
import collection.mutable.ListBuffer
import collection.immutable.{List, SortedSet}

/**
 * Document Me..
 *
 * @author Michael Fortin
 * @version $Id : $
 */
class WebAppConfig(val init: BootConfig,
                   val views: Plugin,
                   val persistence: List[Plugin],
                   val plugins: List[Plugin])
        extends WebXml {
  val environment: String = init.environment.get
  val application: Application = init.application.getOrElse(null)
  val project: Project = init.project.getOrElse(null)
  val logging: Logging = init.logging.getOrElse(null)

  val dependencies: SortedSet[Dependency] = {
    val dependencyBuffer = ListBuffer[Dependency]()

    if (init.dependencies.isDefined)
      dependencyBuffer ++= init.dependencies.get

    if (views.dependencies.isDefined)
      dependencyBuffer ++= views.dependencies.get


    persistence.map(plugin => {
      if (plugin.dependencies.isDefined) {
        val depsList: List[Dependency] = plugin.dependencies.get
        depsList.foreach(dep => dependencyBuffer += dep)
      }
    })
    plugins.map(plugin => {
      if (plugin.dependencies.isDefined) {
        val depsList: List[Dependency] = plugin.dependencies.get
        depsList.foreach(dep => dependencyBuffer += dep)
      }
    })
    SortedSet(dependencyBuffer: _*)
  }

  val repositories: SortedSet[Repository] = {
    val repositoryBuffer = ListBuffer[Repository]()

    if (init.repositories.isDefined)
      repositoryBuffer ++= init.repositories.get

    if (views.repositories.isDefined)
      repositoryBuffer ++= views.repositories.get

    persistence.map(plugin => {
      if (plugin.repositories.isDefined) {
        val depsList: List[Repository] = plugin.repositories.get
        depsList.foreach(dep => repositoryBuffer += dep)
      }
    })
    plugins.map(plugin => {
      if (plugin.repositories.isDefined) {
        val depsList: List[Repository] = plugin.repositories.get
        depsList.foreach(dep => repositoryBuffer += dep)
      }
    })
    SortedSet(repositoryBuffer: _*)
  }

  override val webXml: Option[List[Map[String, AnyRef]]] = {
    val buf = ListBuffer[Map[String, AnyRef]]()

    if(views.isInstanceOf[WebXml] && views.asInstanceOf[WebXml].webXml.isDefined)
      buf ++= views.asInstanceOf[WebXml].webXml.get

    if(init.webXml.isDefined)
      buf ++= init.webXml.get

    persistence.foreach(p => {
      if (p.isInstanceOf[WebXml] && p.asInstanceOf[WebXml].webXml.isDefined)
        p.asInstanceOf[WebXml].webXml.get.foreach(xml => buf += xml)
    })

    plugins.foreach(p => {
      if (p.isInstanceOf[WebXml] && p.asInstanceOf[WebXml].webXml.isDefined)
        p.asInstanceOf[WebXml].webXml.get.foreach(xml => buf += xml)
    })
    Some(buf.toList)
  }
}