package org.brzy.webapp

import org.brzy.config.common.{Config, BootConfig}
import java.util.{Map => JMap, HashMap => JHashMap, List => JList, ArrayList => JArrayList}
import org.ho.yaml.Yaml
import java.net.URL
import java.lang.reflect.Constructor
import org.brzy.config.plugin.Plugin
import org.brzy.util.NestedCollectionConverter._
import org.brzy.util.UrlUtils._
import org.brzy.util.FileUtils._
import org.brzy.config.webapp.WebAppConfig
import java.io.{InputStream, File}
import org.slf4j.LoggerFactory

/**
 * Document Me..
 *
 * @author Michael Fortin
 * @version $Id : $
 */
object ConfigFactory {

  private val log = LoggerFactory.getLogger(getClass)
  
  /**
   *
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

    val defaultConfig: BootConfig = {
      new BootConfig(defaultConfigMap)
    }

    val environmentConfig: BootConfig = {
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

    val configmerge: BootConfig = applicationConfig << environmentConfig
    val configmerge2: BootConfig = defaultConfig << configmerge
    configmerge2
  }

  /**
   *
   */
  def makeWebAppConfig(c: BootConfig, view: Plugin, persistence: List[Plugin], plugins: List[Plugin]) = {
    new WebAppConfig(c, view, persistence, plugins)
  }

  def makePlugin(reference: Plugin, pluginFile: File): Plugin = {
    // check classpath at runtime
    val cpUrl = getClass.getClassLoader.getResource("plugins/" + reference.name + "/brzy-plugin.b.yml")
    val yaml = convertMap(Yaml.load(pluginFile).asInstanceOf[JMap[String, AnyRef]])
    val configClass: String = yaml.get("config_class").get.asInstanceOf[String]
    val pluginClass = Class.forName(configClass).asInstanceOf[Class[_]]
    val constructor: Constructor[_] = pluginClass.getConstructor(classOf[Map[String, AnyRef]])
    val newPluginInstance = constructor.newInstance(yaml)
    newPluginInstance.asInstanceOf[Plugin]
  }

  /**
   *
   */
  def writeConfigToFile(config: Config, yamlFile: File) = {
    val jmap = new JHashMap[String, AnyRef]()
    to(config.asMap, jmap)
    val DS = System.getProperty("file.separator")

    Yaml.dump(jmap, yamlFile, true)
  }

  /**
   *
   */
  def installPlugin(destDir: File, plugin: Plugin): Unit = {

    // from local file system for development mode
    if (plugin.localLocation.isDefined) {
      val file = new File(plugin.localLocation.get + "/brzy-plugin.b.yml")

      if (!file.exists)
        error("No plugin file found at Local location: " + plugin.localLocation.get)
    }
    // copy from local system or from remote system for development mode
    else if (plugin.remoteLocation.isDefined) {
      downloadAndUnzipTo(plugin, destDir)
    }
    // lookup via maven repository, the default way
    else {
      // [org(replace . with /)] / [name] / [version] / [name]-[version]-plugin.zip
      val remoteUrl = "http://brzy.org/nexus/" +
              plugin.org.get.replaceAll("\\.", "/") + "/" +
              plugin.name + "/" +
              plugin.version.get + "/" +
              plugin.name + "-" +
              plugin.version.get + "-plugin.zip"

      downloadAndUnzipTo(plugin, remoteUrl, destDir)
    }
  }

  def fileForPlugin(plugin: Plugin): File = {
    log.debug("plugin: {}" , plugin)
    val url = getClass.getClassLoader.getResource(plugin.name.get + "/brzy-plugin.b.yml")
    log.debug("url: {}" , url)
    new File(url.toURI)
  }

  /**
   * This downloads the plugin and expands it in the output directory unless this
   * plugin has a local_location.  In which case the local location is used instead
   * of the plugin cache.
   */
  private def downloadAndUnzipTo(plgn: Plugin, outputDir: File): Unit = {
    // if it has a local location ignore it.
    // downloads from web
    if (plgn.remoteLocation.isDefined && plgn.remoteLocation.get.startsWith("http")) {
      val destinationFile = new URL(plgn.remoteLocation.get).downloadToDir(outputDir)
      destinationFile.unzip()
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
    }
  }

  private def downloadAndUnzipTo(plgn: Plugin, remoteUrl: String, appPluginCache: File) = {
    val destinationFile = new URL(remoteUrl).downloadToDir(new File(appPluginCache, plgn.name.get))
    destinationFile.unzip()
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
          to(m.asInstanceOf[Map[String,AnyRef]], map)
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
        case m: Map[_,_] =>
          val map = new JHashMap[String, AnyRef]()
          to(m.asInstanceOf[Map[String,AnyRef]], map)
          jlist.add(map)
        case l: List[_] =>
          val list = new JArrayList[AnyRef]()
          to(l.asInstanceOf[List[AnyRef]], list)
          jlist.add(list)
        case (n:String, v:String) =>
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