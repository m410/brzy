package org.brzy.config

import org.ho.yaml.Yaml
import java.net.URL
import java.lang.String
import java.io._
import collection.JavaConversions._
import org.slf4j.LoggerFactory
import org.brzy.util.UrlUtils._
import org.brzy.util.FileUtils._
import java.lang.reflect.Constructor
import collection.mutable.ListBuffer
import java.util.{Map => JMap}
import org.brzy.plugin.Plugin
import org.brzy.util.NestedCollectionConverter._


/**
 * Creates the configuration for a brzy application.  This first loads the application config,
 * the default config, then the plugin configs from the listed in the application config, and
 * lastly the environment overrides for the application configuration.  Then
 * It merges them all together into one configuration.
 *
 * webapp = baseWebappConfig <- applicationWebappConfig
 * webapp = webapp <- viewPlugin (default) <- app view plugin
 * webapp = webapp <- persistencePlugins (default) <- app persistence plugins
 * webapp = webapp <- generalPlugins (default) <- app general plugins
 * webapp = webapp <- environment Config (overwrites all)
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


  private val webappConfigMap: Map[String, AnyRef] = {
    val load = Yaml.load(appFile).asInstanceOf[JMap[String,AnyRef]]
    convertMap(load)
  }
  private val defaultConfigMap: Map[String, AnyRef] = {
    val asStream: InputStream = getClass.getClassLoader.getResourceAsStream("brzy-app.default.b.yml")
    val load = Yaml.load(asStream).asInstanceOf[JMap[String,AnyRef]]
    convertMap(load)
  }

  /**
   *
   */
  val applicationConfig:WebappConfig = {
    new WebappConfig(webappConfigMap ++ Map[String,String]("environment" -> environment))
  }

  /**
   *
   */
  val defaultConfig:WebappConfig = {
    new WebappConfig(defaultConfigMap)
  }

  /**
   *
   */
  lazy val environmentConfig: WebappConfig = {
    val list = webappConfigMap.get("environment_overrides").get.asInstanceOf[List[Map[String,AnyRef]]]
    val option = list.find(innermap => {
      val tuple = innermap.find(hm => hm._1 == "environment").get
      tuple._2.asInstanceOf[String].compareTo(environment) == 0
    })

    if (option.isDefined)
      new WebappConfig(option.get.asInstanceOf[Map[String, AnyRef]])
    else
      error("Unknown Environment: '" + environment + "' must be one of [test,development,production]")
  }

  /**
   * The plugin downloaded and installed, with default parameters
   */
  lazy val pluginConfigs: List[Plugin] = {
    val plugins = collection.mutable.ListBuffer[Plugin]()

    if (applicationConfig.plugins.isDefined)
      applicationConfig.plugins.get.foreach(plugin => {
        plugins += loadPlugin(plugin)
      })

    plugins.toList
  }

  /**
   *
   */
  lazy val viewPluginConfig:Plugin = {
    val plugin =
    if (applicationConfig.views.isDefined)
      applicationConfig.views.get
    else
      defaultConfig.views.get

    loadPlugin(plugin)
  }

  /**
   *
   */
  lazy val persistencePluginConfigs = {
    val plugins = collection.mutable.ListBuffer[Plugin]()

    if (applicationConfig.persistence.isDefined)
      applicationConfig.plugins.foreach(plugin => {
        val p = plugin.asInstanceOf[Plugin]
        plugins += loadPlugin(p)
      })

    plugins.toList
  }


  /**
   * Adds all the other configurations to the application configuration.  Properties are
   * added to the application but do not over write them.
   */
  lazy val runtimeConfig = {
    var config = defaultConfig << applicationConfig
    val newView = viewPluginConfig << applicationConfig.views.get
    config = config << new WebappConfig(Map[String, AnyRef]("views" -> newView.asMap))

    var merged = new ListBuffer[Plugin]()
    for (i <- 0 to persistencePluginConfigs.length)
      merged += persistencePluginConfigs(i) << applicationConfig.persistence.get(i)

    merged.foreach(merge => {
      config = config << new WebappConfig(Map[String, AnyRef]("persistence" -> merge))
    })

    var merged2 = new ListBuffer[Plugin]()
    for (i <- 0 to pluginConfigs.length)
      merged2 += pluginConfigs(i) << applicationConfig.plugins.get(i)

    merged2.foreach(merge => {
      config = config << new WebappConfig(Map[String, AnyRef]("plugins" -> merge))
    })

    config << environmentConfig
  }

  protected def loadPlugin(plugin: Plugin): Plugin = {

    // check classpath at runtime
    val cpUrl = getClass.getClassLoader.getResource("plugins/" + plugin.name + "/brzy-plugin.b.yml")

    val pluginHost =
    if (applicationConfig.project.isDefined && applicationConfig.project.get.pluginRepository.isDefined)
      applicationConfig.project.get.pluginRepository.get
    else
      defaultConfig.project.get.pluginRepository.get

    val appPluginCache =
    if (applicationConfig.project.isDefined && applicationConfig.project.get.pluginResources.isDefined)
      new File(appFile.getParent, applicationConfig.project.get.pluginResources.get)
    else
      new File(appFile.getParent, defaultConfig.project.get.pluginResources.get)

    val pluginFile: File =
    // check classpath first for runtime config files
    if (cpUrl != null) {
      new File(cpUrl.getFile)
    }
    // from local file system for developement mode
    else if (plugin.localLocation.isDefined) {
      new File(appFile.getParentFile, plugin.localLocation.get + "/brzy-plugin.b.yml")
    }
    // copy from local system or from remote system for developement mode
    else if (plugin.remoteLocation.isDefined) {
      downloadAndUnzipTo(plugin, appPluginCache)
      new File(appPluginCache, plugin.name.get + "/brzy-plugin.b.yml")
    }
    // lookup via maven repository, the default way
    else {
      // [org(. to /)] / [name] / [version] / [name]-[version]-plugin.zip
      val remoteUrl = pluginHost + "/" +
              plugin.org.get.replaceAll("\\.", "/") + "/" +
              plugin.name.get + "/" +
              plugin.version.get + "/" +
              plugin.name.get + "-" +
              plugin.version.get + "-plugin.zip"

      downloadAndUnzipTo(plugin, remoteUrl, appPluginCache)
      new File(appPluginCache, plugin.name.get + "/brzy-plugin.b.yml")
    }

    //    if (pluginFile == null || !pluginFile.exists) {
    //      plugin
    //    }
    //    else {
    val yaml = convertMap(Yaml.load(pluginFile).asInstanceOf[JMap[String,AnyRef]])
    val configClass: String = yaml.get("config_class").get.asInstanceOf[String]
    val pluginClass = Class.forName(configClass).asInstanceOf[Class[_]]
    val constructor: Constructor[_] = pluginClass.getConstructor(classOf[Map[String, AnyRef]])
    val newPluginInstance = constructor.newInstance(yaml)
    newPluginInstance.asInstanceOf[Plugin]
    //    }
  }


  /**
   * This downloads the plugin and expands it in the output directory unless this
   * plugin has a local_location.  In which case the local location is used instead
   * of the plugin cache.
   */
  private[config] def downloadAndUnzipTo(plgn: Plugin, outputDir: File) =
  // if it has a local location ignore it.
    if (plgn.localLocation.isDefined) {
      val file = new File(plgn.localLocation.get)

      if (!file.exists)
        error("No Local plugin at location: " + plgn.localLocation.get)
    }
    // downloads from web
    else if (plgn.remoteLocation.isDefined && plgn.remoteLocation.get.startsWith("http")) {
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

  private[config] def downloadAndUnzipTo(plgn: Plugin, remoteUrl: String, appPluginCache: File) = {
    val destinationFile = new URL(remoteUrl).downloadToDir(new File(appPluginCache, plgn.name.get))
    destinationFile.unzip()
  }
}
