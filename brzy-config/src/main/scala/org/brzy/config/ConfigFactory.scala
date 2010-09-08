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
package org.brzy.config

import org.brzy.config.common.{Config, BootConfig}
import org.brzy.config.mod.Mod
import org.brzy.util.NestedCollectionConverter._
import org.brzy.util.UrlUtils._
import org.brzy.util.FileUtils._
import org.brzy.config.webapp.WebAppConfig

import org.ho.yaml.Yaml

import java.io.{InputStream, File}
import java.util.{Map => JMap, HashMap => JHashMap, List => JList, ArrayList => JArrayList}
import java.net.URL
import java.lang.reflect.Constructor

import org.slf4j.LoggerFactory

/**
 * Creates a configuration from the brzy-webapp.b.yml configuration file.  It can be
 * used in two different scenarios.  In the running web application in the WebAppLister
 * class or in the build system.
 *
 * @author Michael Fortin
 */
object ConfigFactory {
  private val log = LoggerFactory.getLogger(getClass)

  /**
   * Make the boot configuration.
   */
  def makeBootConfig(appFile: File, environment: String): BootConfig = {

    val webappConfigMap: Map[String, AnyRef] = {
      val load = Yaml.load(appFile).asInstanceOf[JMap[String, AnyRef]]
      convertMap(load)
    }

    val defaultConfigMap: Map[String, AnyRef] = {
      val asStream: InputStream = getClass.getClassLoader.getResourceAsStream("brzy-webapp.default.b.yml")
      val load = Yaml.load(asStream).asInstanceOf[JMap[String, AnyRef]]
      convertMap(load)
    }

    val applicationConfig: BootConfig = {
      new BootConfig(webappConfigMap ++ Map[String, String]("environment" -> environment))
    }

    val defaultConfig: BootConfig = new BootConfig(defaultConfigMap)

    val environmentConfig: BootConfig =
      if (webappConfigMap.get("environment_overrides").isDefined) {
        val list = webappConfigMap.get("environment_overrides").get.asInstanceOf[List[Map[String, AnyRef]]]
        val option = list.find(innermap => {
          val tuple = innermap.find(hm => hm._1 == "environment").get
          tuple._2.asInstanceOf[String].compareTo(environment) == 0
        })

        if (option.isDefined)
          new BootConfig(option.get.asInstanceOf[Map[String, AnyRef]])
        else
          new BootConfig(Map[String, AnyRef]())
      }
      else {
        new BootConfig(Map[String, AnyRef]())
      }

    val configmerge: BootConfig = applicationConfig << environmentConfig
    val configmerge2: BootConfig = defaultConfig << configmerge
    configmerge2
  }

  /**
   *  Loads the web application class
   */
  def makeWebAppConfig(c: BootConfig, view: Mod, persistence: List[Mod], mods: List[Mod]) = {
    new WebAppConfig(c, view, persistence, mods)
  }

  /**
   * Loads the application configuration from the classpath
   */
  def makeRuntimeModule(reference: Mod): Mod = {
    val modResource: String = "brzy-modules/" + reference.name.get + "/brzy-module.b.yml"
    val cpUrl = getClass.getClassLoader.getResource(modResource)
    val yaml = convertMap(Yaml.load(cpUrl.openStream).asInstanceOf[JMap[String, AnyRef]])

    if (yaml.get("config_class").isDefined && yaml.get("config_class").get != null) {
      val configClass: String = yaml.get("config_class").get.asInstanceOf[String]
      val modClass = Class.forName(configClass).asInstanceOf[Class[_]]
      val constructor: Constructor[_] = modClass.getConstructor(classOf[Map[String, AnyRef]])
      val newModuleInstance = constructor.newInstance(yaml).asInstanceOf[Mod]
      newModuleInstance << reference
    }
    else {
      reference
    }
  }

  /**
   * Loads the application configuration from the file system
   */
  def makeBuildTimeModule(reference: Mod, modResourceDir: File): Mod = {
    val pFile = new File(modResourceDir, reference.name.get)
    val modFile = new File(pFile, "brzy-module.b.yml")
    val yaml = convertMap(Yaml.load(modFile).asInstanceOf[JMap[String, AnyRef]])

    if (yaml.get("config_class").isDefined && yaml.get("config_class").get != null) {
      val configClass: String = yaml.get("config_class").get.asInstanceOf[String]



      val loader = this.getClass.getClassLoader
      val modClass = loader.loadClass(configClass).asInstanceOf[Class[_]]

//      val modClass = Class.forName(configClass).asInstanceOf[Class[_]]
      val constructor: Constructor[_] = modClass.getConstructor(classOf[Map[String, AnyRef]])
      val newModuleInstance = constructor.newInstance(yaml).asInstanceOf[Mod]
      newModuleInstance << reference
    }
    else {
      reference
    }
  }

  /**
   *  Writes the merged configurations to a file.
   */
  def writeConfigToFile(config: Config, yamlFile: File) = {
    val jmap = new JHashMap[String, AnyRef]()
    to(config.asMap, jmap)
    val DS = System.getProperty("file.separator")

    Yaml.dump(jmap, yamlFile, true)
  }

  /**
   * Downloads the declared modules in the configuration.
   */
  def installModule(destDir: File, mod: Mod): Unit = {

    // from local file system for development mode
    if (mod.localLocation.isDefined && mod.localLocation.get != null) {
      val file = new File(mod.localLocation.get + "/brzy-module.b.yml")

      if (!file.exists)
        error("No module file found: '" + mod + "', location: " + mod.localLocation.get)
    }
    // copy from local system or from remote system for development mode
    else if (mod.remoteLocation.isDefined && mod.remoteLocation.get != null) {
      downloadAndUnzipTo(mod, destDir)
    }
    // lookup via maven repository, the default way
    else {
      // [org(replace . with /)] / [name] / [version] / [name]-[version]-module.zip
      val remoteUrl = "http://brzy.org/nexus/content/repositories/releases/" +
              mod.org.get.replaceAll("\\.", "/") + "/" +
              mod.name.get + "/" +
              mod.version.get + "/" +
              mod.name.get + "-" +
              mod.version.get + "-module.jar"

      downloadAndUnzipTo(mod, remoteUrl, destDir)
    }
  }

  def fileForModule(mod: Mod): File = {
    val url = getClass.getClassLoader.getResource(mod.name.get + "/brzy-module.b.yml")
    new File(url.toURI)
  }

  /**
   * This downloads the module and expands it in the output directory unless this
   * module has a local_location.  In which case the local location is used instead
   * of the module cache.
   */
  private def downloadAndUnzipTo(plgn: Mod, outputDir: File): Unit = {
    // if it has a local location ignore it.
    // downloads from web
    if (plgn.remoteLocation.isDefined && plgn.remoteLocation.get.startsWith("http")) {
      val destinationFile = new URL(plgn.remoteLocation.get).downloadToDir(outputDir)
      destinationFile.unzip()
      destinationFile.delete()
    }
    // copy from local file system
    else {
      val remoteLoc: String = plgn.remoteLocation.get

      val sourceFile =
      if (remoteLoc.startsWith("~")) {
        val home = new File(System.getProperty("user.home"))
        new File(home, remoteLoc.substring(1, remoteLoc.length))
      }
      else
        new File(remoteLoc)

      val destinationFolder = new File(outputDir, plgn.name.get)

      if (!destinationFolder.exists)
        destinationFolder.mkdirs

      val filename = remoteLoc.substring(remoteLoc.lastIndexOf("/"), remoteLoc.length)
      val destinationFile = new File(destinationFolder, filename)

      sourceFile.copyTo(destinationFolder)
      destinationFile.unzip()
      destinationFile.delete()
    }
  }

  private def downloadAndUnzipTo(plgn: Mod, remoteUrl: String, appModuleCache: File) = {
    val destinationFile = new URL(remoteUrl).downloadToDir(new File(appModuleCache, plgn.name.get))
    destinationFile.unzip()
    destinationFile.delete()
  }

  private def to(map: Map[String, AnyRef], jm: JMap[String, AnyRef]): Unit = {
    map.foreach(nvp => {
      nvp._2 match {
        case None =>
        case Some(s) =>
          if (s != null && s.isInstanceOf[String])
            jm.put(nvp._1, s.asInstanceOf[String])
          else if (s != null && s.isInstanceOf[List[_]]) {
            val list = new JArrayList[AnyRef]()
            to(s.asInstanceOf[List[AnyRef]], list)
            jm.put(nvp._1, list)
          }
          else if (s != null && s.isInstanceOf[Map[_, _]]) {
            val map = new JHashMap[String, AnyRef]()
            to(s.asInstanceOf[Map[String, AnyRef]], map)
            jm.put(nvp._1, map)
          }
        case m: Map[_, _] =>
          val map = new JHashMap[String, AnyRef]()
          to(m.asInstanceOf[Map[String, AnyRef]], map)
          jm.put(nvp._1, map)
        case l: List[_] =>
          val list = new JArrayList[AnyRef]()
          to(l.asInstanceOf[List[AnyRef]], list)
          jm.put(nvp._1, list)
        case _ =>
          jm.put(nvp._1, nvp._2)
      }
    })
  }

  private def to(slist: List[AnyRef], jlist: JList[AnyRef]): Unit = {
    slist.foreach(entry => {
      entry match {
        case None =>
        case Some(s) =>
          if (s != null && s.isInstanceOf[String])
            jlist.add(s.asInstanceOf[String])
          else if (s != null && s.isInstanceOf[List[_]]) {
            val list = new JArrayList[AnyRef]()
            to(s.asInstanceOf[List[AnyRef]], list)
            jlist.add(list)
          }
          else if (s != null && s.isInstanceOf[Map[_, _]]) {
            val map = new JHashMap[String, AnyRef]()
            to(entry.asInstanceOf[Map[String, AnyRef]], map)
            jlist.add(map)
          }
        case m: Map[_, _] =>
          val map = new JHashMap[String, AnyRef]()
          to(m.asInstanceOf[Map[String, AnyRef]], map)
          jlist.add(map)
        case l: List[_] =>
          val list = new JArrayList[AnyRef]()
          to(l.asInstanceOf[List[AnyRef]], list)
          jlist.add(list)
        case (n: String, v: String) =>
          val map = new JHashMap[String, AnyRef]()
          if (v.isInstanceOf[List[_]])
            map.put(n, new JArrayList())
          else
            map.put(n, v)
          jlist.add(map)
        case _ =>
          jlist.add(entry)
      }
    })
  }
}