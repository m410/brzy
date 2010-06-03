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
  val application: Application = init.application.get
  val project: Project = init.project.get
  val logging: Logging = init.logging.get

  val dependencies:SortedSet[Dependency] = {
    val dependencyBuffer = ListBuffer[Dependency]()
    dependencyBuffer ++= init.dependencies.get
    dependencyBuffer ++= views.dependencies.get 
    persistence.map(plugin=> {
        val depsList: List[Dependency] = plugin.dependencies.get
        depsList.foreach(dep=> dependencyBuffer += dep)
    })
    plugins.map(plugin=> {
        val depsList: List[Dependency] = plugin.dependencies.get
        depsList.foreach(dep=> dependencyBuffer += dep)
    })
    SortedSet(dependencyBuffer:_*)
  }

  val repositories:SortedSet[Repository] = {
    val repositoryBuffer = ListBuffer[Repository]()
    repositoryBuffer ++= init.repositories.get
    repositoryBuffer ++= views.repositories.get
    persistence.map(plugin=> {
        val depsList: List[Repository] = plugin.repositories.get
        depsList.foreach(dep=> repositoryBuffer += dep)
    })
    plugins.map(plugin=> {
        val depsList: List[Repository] = plugin.repositories.get
        depsList.foreach(dep=> repositoryBuffer += dep)
    })
    SortedSet(repositoryBuffer:_*)
  }

  override val webXml:List[Map[String,AnyRef]] = {
    val buf = ListBuffer[Map[String,AnyRef]]()
    buf ++= views.asInstanceOf[WebXml].webXml
    buf ++= init.webXml.get

    persistence.foreach(p=> {
      if(p.isInstanceOf[WebXml])
        p.asInstanceOf[WebXml].webXml.foreach(xml => buf += xml)
    })

    plugins.foreach(p=> {
      if(p.isInstanceOf[WebXml])
        p.asInstanceOf[WebXml].webXml.foreach(xml => buf += xml)
    })
    buf.toList
  }
}