package org.brzy.config

import org.ho.yaml.Yaml
import java.net.URL
import java.lang.String
import java.io._
import collection.mutable.ListBuffer
import org.slf4j.LoggerFactory

/**
 * Creates the configuration for a brzy application.  This first loads the application config,
 * the default config, then the plugin configs from the listed in the application config, and
 * lastly the environment overrides for the application configuration.  Then
 * It merges them all together into one configuration.
 *
 * @author Michael Fortin
 * @version $Id: $
 */
class Builder(appFile:File, environment:String) {

  def this(conf:String, environment:String) = this(new File(conf),environment)

  def this(conf:URL, environment:String) =  this(new File(conf.getFile),environment)

  private val log = LoggerFactory.getLogger(getClass)
  private val dev = "developement"
  private val prod = "production"
  private val test = "test"

  assert(appFile != null,"configuration file is null")
  assert(appFile.exists,"configuration file doesn not exist: " + appFile.getAbsolutePath)

  val applicationConfig = {
		val config = Yaml.loadType(appFile, classOf[AppConfig])
		config.environment = "app"
		config
	}

  val defaultConfig = Yaml.loadType(
    getClass.getClassLoader.getResourceAsStream("brzy-app.default.b.yml"),
    classOf[AppConfig])

  val pluginConfigs = {
    val plugins = ListBuffer[PluginConfig]()

    if(applicationConfig.plugins != null)
      applicationConfig.plugins.foreach(plugin => plugins += loadPlugin(plugin))

    if(applicationConfig.persistence != null)
      applicationConfig.persistence.foreach(plugin => plugins += loadPlugin(plugin))

    val viewPlugin = new PluginConfig()
    viewPlugin.name = defaultConfig.views.implementation
    viewPlugin.remote_location = defaultConfig.views.remote_location
    viewPlugin.properties = new java.util.HashMap[String,String]
    viewPlugin.properties.put("html_version",defaultConfig.views.html_version) 
    viewPlugin.properties.put("file_extension",defaultConfig.views.file_extension) 
    plugins += loadPlugin(viewPlugin)

    plugins.toArray
  }

  def loadPlugin(plugin:PluginConfig):PluginConfig = {

    val pluginCache =
      if(applicationConfig.project != null && applicationConfig.project.plugin_resources != null)
        new File(appFile.getParent, applicationConfig.project.plugin_resources)
      else
        new File(appFile.getParent, defaultConfig.project.plugin_resources)

//    log.debug("pluginCache: " + pluginCache)

    val pluginFile:File =
      if(plugin.local_location != null)
        new File(appFile.getParentFile, plugin.local_location + "/brzy-plugin.b.yml")
      else  if(plugin.remote_location != null) {
        plugin.downloadAndUnzipTo(pluginCache)
        new File(pluginCache, plugin.name + "/brzy-plugin.b.yml")
      }
      else
        null // TODO need to set the default

//    log.debug("pluginFile: " + pluginFile)

    if(pluginFile == null || !pluginFile.exists) {
//      log.warn("Plugin does not exist: " + pluginFile)
      plugin
    }
    else {
      plugin +  new PluginConfig(pluginFile)
    }
  }

  val environmentConfig = environment match {
    case "development" =>
      applicationConfig.environment_overrides.find(x => x.environment == dev).get
    case "test" =>
      applicationConfig.environment_overrides.find(_.environment == test).get
    case "production" =>
      applicationConfig.environment_overrides.find(_.environment == prod).get
    case _ =>
      error("Unknown Environment: '" + environment +"', must be [developement,test,production]")
  }

  /**
   * Adds all the other configurations to the application configuration.  Properties are
   * added to the application but do not over write them.
   */
  val runtimeConfig = defaultConfig + applicationConfig + environmentConfig ++ pluginConfigs 
}
