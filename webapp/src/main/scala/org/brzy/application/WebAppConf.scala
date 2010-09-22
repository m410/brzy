/*
 * Copyright 2010 Michael Fortin <mike@brzy.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.brzy.application

import org.brzy.fab.conf._
import collection.mutable.ListBuffer
import collection.SortedSet
import org.brzy.fab.reflect.Construct
import java.io.File

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
class WebAppConf(val c: WebAppConfFile, val views: ViewMod, val persistence: List[PersistenceMod], val modules: List[RuntimeMod]) {
  val environment: String = c.environment.get

  val application: Application = c.application.getOrElse(null)

  val project: Project = c.project.getOrElse(null)

  val logging: Logging = c.logging.getOrElse(null)

  val dependencies: SortedSet[Dependency] = {
    val dependencyBuffer = ListBuffer[Dependency]()

    if (c.dependencies.isDefined)
      dependencyBuffer ++= c.dependencies.get

    if (views != null && views.dependencies.isDefined)
      dependencyBuffer ++= views.dependencies.get


    persistence.map(mod => {
      if (mod.dependencies.isDefined) {
        val depsList: List[Dependency] = mod.dependencies.get
        depsList.foreach(dep => dependencyBuffer += dep)
      }
    })

    modules.map(mod => {
      if (mod.dependencies.isDefined) {
        val depsList: List[Dependency] = mod.dependencies.get
        depsList.foreach(dep => dependencyBuffer += dep)
      }
    })
    SortedSet(dependencyBuffer: _*)
  }

  val dependencyExcludes: SortedSet[Dependency] = {
    val dependencyBuffer = ListBuffer[Dependency]()

    if (c.dependencyExcludes.isDefined)
      dependencyBuffer ++= c.dependencyExcludes.get

    SortedSet(dependencyBuffer: _*)
  }

  val repositories: SortedSet[Repository] = {
    val repositoryBuffer = ListBuffer[Repository]()
    // TODO, need to filter dependencies with same org, name, but dif revision
    if (c.repositories.isDefined)
      repositoryBuffer ++= c.repositories.get

    if (views != null && views.repositories.isDefined)
      repositoryBuffer ++= views.repositories.get

    persistence.map(mod => {
      if (mod.repositories.isDefined) {
        val depsList: List[Repository] = mod.repositories.get
        depsList.foreach(dep => repositoryBuffer += dep)
      }
    })
    modules.map(mod => {
      if (mod.repositories.isDefined) {
        val depsList: List[Repository] = mod.repositories.get
        depsList.foreach(dep => repositoryBuffer += dep)
      }
    })
    SortedSet(repositoryBuffer: _*)
  }

  val webXml: Option[List[Map[String, AnyRef]]] = {
    val buf = ListBuffer[Map[String, AnyRef]]()

    if (views != null && views.isInstanceOf[WebXml] && views.asInstanceOf[WebXml].webXml.isDefined)
      buf ++= views.asInstanceOf[WebXml].webXml.get

    if (c.webXml.isDefined)
      buf ++= c.webXml.get

    persistence.foreach(p => {
      if (p.isInstanceOf[WebXml] && p.asInstanceOf[WebXml].webXml.isDefined)
        p.asInstanceOf[WebXml].webXml.get.foreach(xml => buf += xml)
    })

    modules.foreach(p => {
      if (p.isInstanceOf[WebXml] && p.asInstanceOf[WebXml].webXml.isDefined)
        p.asInstanceOf[WebXml].webXml.get.foreach(xml => buf += xml)
    })
    Some(buf.toList)
  }
}

/**
 *
 */
object WebAppConf {
  val defaultConfigFile = "/brzy-webapp.default.b.yml"
  val appConfigFile = "/brzy-webapp.b.yml"

  /**
   *
   */
  def apply(env: String, appConfig: String = appConfigFile, defaultConfig: String = defaultConfigFile) = {
    val defaultConf = new WebAppConfFile(Yaml(getClass.getResourceAsStream(defaultConfig)))
    val appConf = new WebAppConfFile(Yaml(getClass.getResourceAsStream(appConfig)))

    val envMap = appConf.map.get("environment_overrides") match {
      case Some(a) => a.asInstanceOf[List[Map[String,AnyRef]]].find(_("environment") == env)
      case _ => None
    }

    val runtimeConfig = envMap match {
      case Some(e) =>
        val envConf = new WebAppConfFile(e.asInstanceOf[Map[String, AnyRef]])
        val runConf =  envConf << appConf << defaultConf
        runConf.asInstanceOf[WebAppConfFile]
      case _ =>
        val runConf = appConf << defaultConf
        runConf.asInstanceOf[WebAppConfFile]
    }

    val view: ViewMod = runtimeConfig.views match {
      case Some(v) =>
        if (v != null)
          makeRuntimeMod(runtimeConfig.views.get).asInstanceOf[ViewMod]
        else
          null
      case _ => null
    }

    val persistence: List[PersistenceMod] = {
      if (runtimeConfig.persistence.isDefined)
        runtimeConfig.persistence.get.map(makeRuntimeMod(_).asInstanceOf[PersistenceMod])
      else
        Nil
    }
    val modules: List[RuntimeMod] = {
      if (runtimeConfig.modules.isDefined)
        runtimeConfig.modules.get.map(makeRuntimeMod(_).asInstanceOf[RuntimeMod])
      else
        Nil
    }

    new WebAppConf(runtimeConfig, view, persistence, modules)
  }

  /**
   *
   */
  def buildtime(modBaseDir: File, env: String, appConfig: String = appConfigFile, defaultConfig: String = defaultConfigFile) = {
    val defaultConf = new WebAppConfFile(Yaml(getClass.getResourceAsStream(defaultConfig)))
    val appConf = new WebAppConfFile(Yaml(getClass.getResourceAsStream(appConfig)))

    val envMap = appConf.map.get("environment_overrides") match {
      case Some(a) => a.asInstanceOf[Map[String, AnyRef]].get(env)
      case _ => None
    }

    val buildConfig: WebAppConfFile = envMap match {
      case Some(e) =>
        val envConf = new WebAppConfFile(e.asInstanceOf[Map[String, AnyRef]])
        val runConf = envConf << appConf << defaultConf
        runConf.asInstanceOf[WebAppConfFile]
      case _ =>
        val runConf = appConf << defaultConf
        runConf.asInstanceOf[WebAppConfFile]
    }

    val view: ViewMod = buildConfig.views match {
      case Some(v) =>
        if (v != null)
          makeBuildTimeMod(buildConfig.views.get, modBaseDir).asInstanceOf[ViewMod]
        else
          null
      case _ => null
    }

    val persistence: List[PersistenceMod] = {
      if (buildConfig.persistence.isDefined)
        buildConfig.persistence.get.map(makeBuildTimeMod(_, modBaseDir).asInstanceOf[PersistenceMod])
      else
        Nil
    }
    val modules: List[RuntimeMod] = {
      if (buildConfig.modules.isDefined)
        buildConfig.modules.get.map(makeBuildTimeMod(_, modBaseDir).asInstanceOf[RuntimeMod])
      else
        Nil
    }

    new WebAppConf(buildConfig, view, persistence, modules)
  }


  /**
   * Loads the application configuration from the classpath
   */
  protected[application] def makeRuntimeMod(reference: Mod): Mod = {
    val modResource: String = "modules/" + reference.name.get + "/brzy-module.b.yml"
    val cpUrl = getClass.getClassLoader.getResource(modResource)
    val yaml = Yaml(cpUrl.openStream)

    if (yaml.get("config_class").isDefined && yaml.get("config_class").get != null) {
      val c = Class.forName(yaml.get("config_class").get.asInstanceOf[String])
      val constructor = c.getConstructor(Array(classOf[Map[_,_]]):_*)
      val modInst = constructor.newInstance(yaml).asInstanceOf[Mod]
      val mod = modInst << reference
      mod.asInstanceOf[Mod]
    }
    else {
      reference
    }
  }

  /**
   * Loads the application configuration from the file system
   */
  protected[application] def makeBuildTimeMod(reference: Mod, modResourceDir: File): Mod = {
    val pFile = new File(modResourceDir, reference.name.get)
    val modFile = new File(pFile, "brzy-module.b.yml")
    val yaml = Yaml(modFile)

    if (yaml.get("config_class").isDefined && yaml.get("config_class").get != null) {
      val c = Class.forName(yaml.get("config_class").get.asInstanceOf[String])
      val constructor = c.getConstructor(Array(classOf[Map[_,_]]):_*)
      val modInst = constructor.newInstance(yaml).asInstanceOf[Mod]
      val mod = modInst << reference
      mod.asInstanceOf[Mod]
    }
    else {
      reference
    }
  }

}
