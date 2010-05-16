package org.brzy.config

import org.ho.yaml.Yaml
import java.net.URL
import java.lang.String
import java.io._
import collection.JavaConversions._
import org.slf4j.LoggerFactory
import java.util.{HashMap=>JMap}

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
//  private val dev = "developement"
//  private val prod = "production"
//  private val test = "test"

  assert(appFile != null,"configuration file is null")
  assert(appFile.exists,"configuration file doesn not exist: " + appFile.getAbsolutePath)

  val applicationConfig = {
		val config = Yaml.load(appFile)
    val map = new JMap[String,String]()
    map.put("environment",environment)
		new WebappConfig(config.asInstanceOf[JMap[String,AnyRef]].toMap)
	}

  val defaultConfig = {
    val asStream= getClass.getClassLoader.getResourceAsStream("brzy-app.default.b.yml")
		new WebappConfig(Yaml.load(asStream).asInstanceOf[JMap[String,AnyRef]].toMap)
  }

//
//  val pluginConfigs = {
//    val plugins = ListBuffer[PluginConfig]()
//
//    if(applicationConfig.plugins != null)
//      applicationConfig.plugins.foreach(plugin => plugins += loadPlugin(plugin))
//
//    if(applicationConfig.persistence != null)
//      applicationConfig.persistence.foreach(plugin => plugins += loadPlugin(plugin))
//
//    val viewPlugin = new PluginConfig()
//    viewPlugin.name = defaultConfig.views.name
//    viewPlugin.version = defaultConfig.views.version
//    viewPlugin.group_id = defaultConfig.views.group_id
//    viewPlugin.remote_location = defaultConfig.views.remote_location
//    viewPlugin.properties = new java.util.HashMap[String,String]
//    viewPlugin.properties.put("html_version",defaultConfig.views.html_version)
//    viewPlugin.properties.put("file_extension",defaultConfig.views.file_extension)
//    plugins += loadPlugin(viewPlugin)
//
//    plugins.toArray
//  }
//
//  def loadPlugin(plugin:PluginConfig):PluginConfig = {
//
//    // check classpath at runtime
//    val cpUrl = getClass.getClassLoader.getResource("plugins/"+plugin.name+"/brzy-plugin.b.yml")
//
//    val pluginHost =
//      if(applicationConfig.project != null && applicationConfig.project.pluginRepository != null)
//        applicationConfig.project.pluginRepository
//      else
//        defaultConfig.project.pluginRepository
//
//    val appPluginCache =
//      if(applicationConfig.project != null && applicationConfig.project.pluginResources != null)
//        new File(appFile.getParent, applicationConfig.project.pluginResources)
//      else
//        new File(appFile.getParent, defaultConfig.project.pluginResources)
//
//    val pluginFile:File =
//      // check classpath first for runtime config files
//      if(cpUrl != null) {
//        new File(cpUrl.getFile)
//      }
//      // from local file system for developement mode
//      else if(plugin.local_location != null) {
//        new File(appFile.getParentFile, plugin.local_location + "/brzy-plugin.b.yml")
//      }
//      // copy from local system or from remote system for developement mode
//      else  if(plugin.remote_location != null) {
//        plugin.downloadAndUnzipTo(appPluginCache)
//        new File(appPluginCache, plugin.name + "/brzy-plugin.b.yml")
//      }
//      // lookup via maven repository, the default way
//      else {
//        // [org(. to /)] / [name] / [version] / [name]-[version]-plugin.zip
//        val remoteUrl = pluginHost + "/" +
//                plugin.group_id.replaceAll("\\.","/") + "/" +
//                plugin.name + "/" +
//                plugin.version + "/" +
//                plugin.name + "-" +
//                plugin.version + "-plugin.zip"
//
//        plugin.downloadAndUnzipTo(remoteUrl, appPluginCache)
//        new File(appPluginCache, plugin.name + "/brzy-plugin.b.yml")
//      }
//
//    if(pluginFile == null || !pluginFile.exists) {
//      plugin
//    }
//    else {
//      plugin +  new PluginConfig(pluginFile)
//    }
//  }



  
//  val environmentOverride:AppConfig = environment match {
//    case "development" =>
//      val envList = m.get("environment_overrides").find(ref=> {
//        val list = ref.asInstanceOf[JList[JMap[String,AnyRef]]]
//        val envMap = list.find(hm=> hm.containsKey("environment"))
//        envMap.get("environment") == dev
//      }).get.asInstanceOf[JList[JMap[String,AnyRef]]]
//      var map = collection.mutable.HashMap[String,AnyRef]()
//      envList.foreach((hashmap:JMap[String,AnyRef])=> hashmap.foreach(tuple=> map.put(tuple._1,tuple._2) ))
//      new AppConfig(map.toMap)
//
//    case "test" =>
//      val envList = m.get("environment_overrides").find(ref=> {
//        val list = ref.asInstanceOf[JList[JMap[String,AnyRef]]]
//        val envMap = list.find(hm=> hm.containsKey("environment"))
//        envMap.get("environment") == test
//      }).get.asInstanceOf[JList[JMap[String,AnyRef]]]
//      var map = collection.mutable.HashMap[String,AnyRef]()
//      envList.foreach((hashmap:JMap[String,AnyRef])=> hashmap.foreach(tuple=> map.put(tuple._1,tuple._2) ))
//      new AppConfig(map.toMap)
//
//    case "production" =>
//      val option = m.get("environment_overrides").find(ref=> {
//        val list = ref.asInstanceOf[JList[JMap[String,AnyRef]]]
//        val envMap = list.find(hm=> hm.containsKey("environment"))
//        envMap.get("environment") == prod
//      })
//
//      if(option.isDefined) {
//        val envList = option.get.asInstanceOf[JList[JMap[String,AnyRef]]]
//        var map = collection.mutable.HashMap[String,AnyRef]()
//        envList.foreach((hashmap:JMap[String,AnyRef])=> hashmap.foreach(tuple=> map.put(tuple._1,tuple._2) ))
//        new AppConfig(map.toMap)
//      }
//      else
//        null
//
//    case _ =>
//      error("Unknown Environment: '" + environment +"', must be [developement,test,production]")
//  }

  /**
   * Adds all the other configurations to the application configuration.  Properties are
   * added to the application but do not over write them.
   */
  val runtimeConfig = defaultConfig + applicationConfig //+ environmentConfig ++ pluginConfigs 
}
