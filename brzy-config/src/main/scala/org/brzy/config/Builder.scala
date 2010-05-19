package org.brzy.config

import org.ho.yaml.Yaml
import java.net.URL
import java.lang.String
import java.io._
import collection.JavaConversions._
import org.slf4j.LoggerFactory
import java.util.{HashMap => JMap, List => JList}
import org.brzy.plugin.Plugin
import org.brzy.util.UrlUtils._
import org.brzy.util.FileUtils._
import java.lang.reflect.Constructor

/**
 * Creates the configuration for a brzy application.  This first loads the application config,
 * the default config, then the plugin configs from the listed in the application config, and
 * lastly the environment overrides for the application configuration.  Then
 * It merges them all together into one configuration.
 *
 * @author Michael Fortin
 * @version $Id : $
 */
class Builder(appFile: File, environment: String) {
  def this(conf: String, environment: String) = this (new File(conf), environment)

  def this(conf: URL, environment: String) = this (new File(conf.getFile), environment)

  private val log = LoggerFactory.getLogger(getClass)
  private val dev = "development"
  private val prod = "production"
  private val test = "test"

  assert(appFile != null, "configuration file is null")
  assert(appFile.exists, "configuration file doesn not exist: " + appFile.getAbsolutePath)

  private val webappConfigMap = Yaml.load(appFile)
  private val defaultConfigMap = Yaml.load(getClass.getClassLoader.getResourceAsStream("brzy-app.default.b.yml"))

  lazy val applicationConfig = {
    val map = new JMap[String, String]()
    map.put("environment", environment)
    new WebappConfig(webappConfigMap.asInstanceOf[JMap[String, AnyRef]].toMap)
  }

  lazy val defaultConfig = {
    new WebappConfig(defaultConfigMap.asInstanceOf[JMap[String, AnyRef]].toMap)
  }

  lazy val environmentConfig: WebappConfig = {

    val jmap = webappConfigMap.asInstanceOf[JMap[String, AnyRef]]
    val list = jmap.get("environment_overrides").asInstanceOf[JList[_]]
    val option = list.find(ref => {
      val innermap = ref.asInstanceOf[JMap[String, AnyRef]]
      val tuple = innermap.find(hm => hm._1 == "environment").get
      tuple._2.asInstanceOf[String].compareTo(environment) == 0
    })

    if (option.isDefined)
      new WebappConfig(option.get.asInstanceOf[JMap[String, AnyRef]].toMap)
    else
      error("Unknown Environment: '" + environment + "' must be one of [test,development,production]")
  }



  lazy val pluginConfigs = {
    val plugins = collection.mutable.ListBuffer[Plugin[_]]()

    if (applicationConfig.plugins != null)
      applicationConfig.plugins.foreach(plugin => {
        val p = plugin.asInstanceOf[Plugin[_]]
        plugins += loadPlugin(p)
      })

    // TODO need to set the view and the persistence plugins
    plugins.toArray
  }

  lazy val viewPluginConfig = {}

  lazy val persistencePluginConfigs = {}

  protected def loadPlugin(plugin: Plugin[_]): Plugin[_] = {

    // check classpath at runtime
    val cpUrl = getClass.getClassLoader.getResource("plugins/" + plugin.name + "/brzy-plugin.b.yml")

    val pluginHost =
    if (applicationConfig.project != null && applicationConfig.project.pluginRepository != null)
      applicationConfig.project.pluginRepository
    else
      defaultConfig.project.pluginRepository

    val appPluginCache =
    if (applicationConfig.project != null && applicationConfig.project.pluginResources != null)
      new File(appFile.getParent, applicationConfig.project.pluginResources)
    else
      new File(appFile.getParent, defaultConfig.project.pluginResources)

    val pluginFile: File =
    // check classpath first for runtime config files
    if (cpUrl != null) {
      new File(cpUrl.getFile)
    }
    // from local file system for developement mode
    else if (plugin.localLocation != null) {
      new File(appFile.getParentFile, plugin.localLocation + "/brzy-plugin.b.yml")
    }
    // copy from local system or from remote system for developement mode
    else if (plugin.remoteLocation != null) {
      downloadAndUnzipTo(plugin, appPluginCache)
      new File(appPluginCache, plugin.name + "/brzy-plugin.b.yml")
    }
    // lookup via maven repository, the default way
    else {
      // [org(. to /)] / [name] / [version] / [name]-[version]-plugin.zip
      val remoteUrl = pluginHost + "/" +
              plugin.org.replaceAll("\\.", "/") + "/" +
              plugin.name + "/" +
              plugin.version + "/" +
              plugin.name + "-" +
              plugin.version + "-plugin.zip"

      downloadAndUnzipTo(plugin, remoteUrl, appPluginCache)
      new File(appPluginCache, plugin.name + "/brzy-plugin.b.yml")
    }

    if (pluginFile == null || !pluginFile.exists) {
      plugin
    }
    else {
      val yaml = Yaml.load(pluginFile).asInstanceOf[JMap[String, AnyRef]]
      val configClass: String = yaml.get("config_class").asInstanceOf[String]
      val pluginClass = Class.forName(configClass).asInstanceOf[Class[_]]
      val constructor: Constructor[_] = pluginClass.getConstructor(classOf[Map[String, AnyRef]])
      val newPluginInstance = constructor.newInstance(yaml.toMap)
      plugin + newPluginInstance.asInstanceOf[Plugin[_]]
    }
  }


  /**
   * This downloads the plugin and expands it in the output directory unless this
   * plugin has a local_location.  In which case the local location is used instead
   * of the plugin cache.
   */
  private[config] def downloadAndUnzipTo(plgn: Plugin[_], outputDir: File) =
  // if it has a local location ignore it.
    if (plgn.localLocation != null) {
      val file = new File(plgn.localLocation)

      if (!file.exists)
        error("No Local plugin at location: " + plgn.localLocation)
    }
    // downloads from web
    else if (plgn.remoteLocation != null && plgn.remoteLocation.startsWith("http")) {
      val destinationFile = new URL(plgn.remoteLocation).downloadToDir(outputDir)
      destinationFile.unzip()
    }
    // copy from local file system
    else {
      val remoteLoc: String = plgn.remoteLocation

      val sourceFile =
      if (remoteLoc.startsWith("~")) {
        val home = new File(System.getProperty("user.home"))
        new File(home, remoteLoc.substring(1, remoteLoc.length))
      }
      else
        new File(remoteLoc)

      val destinationFolder = new File(outputDir, plgn.name)

      if (!destinationFolder.exists)
        destinationFolder.mkdirs

      val filename = remoteLoc.substring(remoteLoc.lastIndexOf("/"), remoteLoc.length)
      val destinationFile = new File(destinationFolder, filename)

      sourceFile.copyTo(destinationFolder)
      destinationFile.unzip()
    }

  private[config] def downloadAndUnzipTo(plgn: Plugin[_], remoteUrl: String, appPluginCache: File) = {
    val destinationFile = new URL(remoteUrl).downloadToDir(new File(appPluginCache, plgn.name))
    destinationFile.unzip()
  }

  /**
   * Adds all the other configurations to the application configuration.  Properties are
   * added to the application but do not over write them.
   */
  lazy val runtimeConfig = {
    defaultConfig + applicationConfig + environmentConfig //++ pluginConfigs
  }
}
