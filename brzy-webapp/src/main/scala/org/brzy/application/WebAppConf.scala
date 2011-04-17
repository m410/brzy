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
import java.io.File
import org.brzy.fab.mod.{Mod, PersistenceMod, RuntimeMod, ViewMod}
import org.slf4j.LoggerFactory
import com.twitter.json.Json

/**
 * This is the main webapp configuration class.  It consists of all the parts configured
 * in the brzy-webapp.b.yml configuration file.  The file is available at build time and
 * at runtime.  See the companion class for the factory methods.
 *
 * @author Michael Fortin
 */
class WebAppConf(val c: WebAppConfFile, val views: ViewMod, val persistence: List[PersistenceMod], val modules: List[RuntimeMod])
        extends Configuration {

  /**
   * The developement environment
   */
  val environment: String = c.environment.orNull

  /**
   * The application meta data, like author description and version.
   */
  val application: Application = c.application.orNull

  val build: Build = c.build.orNull

  /**
   * logging information
   */
  val logging: Logging = c.logging.orNull

  /**
   * library dependencies
   */
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

  /**
   * Global library exclusions.
   */
  val dependencyExcludes: SortedSet[Dependency] = {
    val dependencyBuffer = ListBuffer[Dependency]()

    if (c.dependencyExcludes.isDefined)
      dependencyBuffer ++= c.dependencyExcludes.get

    SortedSet(dependencyBuffer: _*)
  }

  /**
   * the repositories from where to retrieve the dependencies.
   */
  val repositories: SortedSet[Repository] = {
    val repositoryBuffer = ListBuffer[Repository]()

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

  /**
   * The Web.xml elements.
   */
  val webXml: List[Map[String, AnyRef]] = {
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
      if (p.isInstanceOf[WebXml] && p.asInstanceOf[WebXml].webXml.isDefined && p.asInstanceOf[WebXml].webXml.get != null)
        p.asInstanceOf[WebXml].webXml.get.foreach(xml => buf += xml)
    })
    buf.toList
  }

  def toJson = {
    val data = Map("webAppConfigFile" -> c.map,
      "views" -> Map("viewClass"->views.getClass.getName,"view"->views.map),
      "persistence" -> persistence.map(p=>{ Map("persistClass"->p.getClass.getName,"persist"->p.map)}),
      "modules" -> modules.map(m=>{Map("modClass"->m.getClass.getName,"mod"->m.map)}))
    Json.build(data).toString()
  }
}

/**
 * Factory methods to create a WebAppConf.  This can create a runtime configuration or a build
 * time configuration.
 *
 * @author Michael Fortin
 */
object WebAppConf {
  private val log = LoggerFactory.getLogger(getClass)
  val defaultConfigFile = "/brzy-webapp.default.b.yml"
  val appConfigFile = "/brzy-webapp.b.yml"

  /**
   *  Create the runtime configuration
   */
  def apply(env: String, appConfig: String = appConfigFile, defaultConfig: String = defaultConfigFile) = {
    val defaultConf = new WebAppConfFile(Yaml(getClass.getResourceAsStream(defaultConfig)))
    val appConf = new WebAppConfFile(Yaml(getClass.getResourceAsStream(appConfig)))

    val envMap = appConf.map.get("environment_overrides") match {
      case Some(a) => a.asInstanceOf[List[Map[String, AnyRef]]].find(_("environment") == env)
      case _ => None
    }

    val runtimeConfig = envMap match {
      case Some(e) =>
        val envConf = new WebAppConfFile(e.asInstanceOf[Map[String, AnyRef]])
        val runConf = defaultConf << appConf << envConf
        runConf.asInstanceOf[WebAppConfFile]
      case _ =>
        val runConf = defaultConf << appConf
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
      if (runtimeConfig.modules.isDefined) {
        val runMod = runtimeConfig.modules.get.map(makeRuntimeMod(_))
        runMod.filter(_.isInstanceOf[RuntimeMod]).map(_.asInstanceOf[RuntimeMod])
      }
      else
        Nil
    }

    new WebAppConf(runtimeConfig, view, persistence, modules)
  }

  /**
   *  Create a build time configuration.
   */
  def buildtime(modBaseDir: File, env: String, appConfig: String = appConfigFile, defaultConfig: String = defaultConfigFile) = {
    val defaultConf = new WebAppConfFile(Yaml(getClass.getResourceAsStream(defaultConfig)))
    val appConf = new WebAppConfFile(Yaml(getClass.getResourceAsStream(appConfig)))

    val envMap = appConf.map.get("environment_overrides") match {
      case Some(a) => a.asInstanceOf[List[Map[String, AnyRef]]].find(_("environment") == env)
      case _ => None
    }

    val buildConfig: WebAppConfFile = envMap match {
      case Some(e) =>
        val envConf = new WebAppConfFile(e.asInstanceOf[Map[String, AnyRef]])
        val runConf = defaultConf << appConf << envConf
        runConf.asInstanceOf[WebAppConfFile]
      case _ =>
        val runConf = defaultConf << appConf
        runConf.asInstanceOf[WebAppConfFile]
    }

    // TODO, should probably be an Option, since it's possible to have a site without a view renderer
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
      if (buildConfig.modules.isDefined) {
        val runMod = buildConfig.modules.get.map(makeBuildTimeMod(_, modBaseDir))
        runMod.filter(_.isInstanceOf[RuntimeMod]).map(_.asInstanceOf[RuntimeMod])
      }
      else {
        Nil
      }
    }

    new WebAppConf(buildConfig, view, persistence, modules)
  }


  /**
   * Loads the application configuration from the classpath
   */
  protected[application] def makeRuntimeMod(reference: Mod): Mod = {
    val modResource: String = "modules/" + reference.name.get + "/brzy-module.b.yml"
    log.debug("module conf: '{}'", modResource)
    val cpUrl = getClass.getClassLoader.getResource(modResource)
    val yaml = Yaml(cpUrl.openStream)

    if (yaml.get("config_class").isDefined && yaml.get("config_class").get != null) {
      // this needs to be in this class, because of classloader scope issues.  Same
      // with the duplicate code below.  This should remove this later once the build runner
      // classloader is ironed out.
      val c = Class.forName(yaml.get("config_class").get.asInstanceOf[String])
      val constructor = c.getConstructor(Array(classOf[Map[_, _]]): _*)
      val modInst = constructor.newInstance(yaml).asInstanceOf[Mod]
      val mod = modInst << reference
      mod.asInstanceOf[Mod]
    }
    else {
      log.warn("No config_class for mod: {}", reference)
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
      val constructor = c.getConstructor(Array(classOf[Map[_, _]]): _*)
      val modInst = constructor.newInstance(yaml).asInstanceOf[Mod]
      val mod = modInst << reference
      mod.asInstanceOf[Mod]
    }
    else {
      reference
    }
  }

  def fromJson(json: String) = {
    val data = Json.parse(json).asInstanceOf[Map[String,AnyRef]]
    val conf = new WebAppConfFile(data("webAppConfigFile").asInstanceOf[Map[String,AnyRef]])

    val v = data("views").asInstanceOf[Map[String,AnyRef]]
    val views = inst[ViewMod](v("viewClass").asInstanceOf[String],v("view").asInstanceOf[Map[String,AnyRef]])

    val persistenceList = data("persistence").asInstanceOf[List[Map[String, AnyRef]]]
    val persistence = persistenceList.map(p=>{
      val v = p.asInstanceOf[Map[String,AnyRef]]
      inst[PersistenceMod](v("persistClass").asInstanceOf[String],v("persist").asInstanceOf[Map[String,AnyRef]])
    })

    val modList = data("modules").asInstanceOf[List[Map[String, AnyRef]]]
    val modules = modList.map(p=>{
      val v = p.asInstanceOf[Map[String,AnyRef]]
      inst[RuntimeMod](v("modClass").asInstanceOf[String],v("mod").asInstanceOf[Map[String,AnyRef]])
    })
    new WebAppConf(conf, views, persistence, modules)
  }

  protected[application] def inst[T](className:String,args:Map[String,AnyRef]) = {
    val clazz = Class.forName(className)
    val constructor = clazz.getConstructor(classOf[Map[String,AnyRef]])
    constructor.newInstance(args).asInstanceOf[T]
  }
}
