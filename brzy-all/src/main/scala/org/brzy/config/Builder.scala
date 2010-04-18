package org.brzy.config

import org.ho.yaml.Yaml
import java.net.URL
import java.lang.String
import java.io._
import collection.mutable.ListBuffer
import org.brzy.application.WebApp

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

  private val dev = "developement"
  private val prod = "production"
  private val test = "test"

  assert(appFile != null,"configuration file is null")
  assert(appFile.exists,"configuration file doesn not exist: " + appFile.getAbsolutePath)

  val applicationConfig = {
		val config = Yaml.loadType(appFile, classOf[Config])
		config.environment = "app"
		config.config_type = "master"
		config
	}

  val defaultConfig = Yaml.loadType(
    getClass.getClassLoader.getResourceAsStream("brzy-app.default.b.yml"),
    classOf[Config])

  val pluginConfigs = {
    val plugins = ListBuffer[Config]()

    if(applicationConfig.plugins != null)
      applicationConfig.plugins.foreach(plugin => plugins += loadPlugin(plugin.name))

    // implicit logging plugin
    plugins += loadPlugin(
      if(applicationConfig.logging.provider != null)
        applicationConfig.logging.provider
      else
        defaultConfig.logging.provider
      )

    // implicit persistence plugin(s)
    if(applicationConfig.logging != null)
      plugins += loadPlugin(applicationConfig.logging.provider)

    // implicit view plugin
//    if(applicationConfig.logging != null)
//      plugins += loadPlugin(applicationConfig.logging.provider)

    plugins.toArray
  }

  val environmentConfig = environment match {
    case "development" =>
      applicationConfig.environment_overrides.find(x => x.environment == dev).get
    case "test" =>
      applicationConfig.environment_overrides.find(_.environment == test).get
    case "production" =>
      applicationConfig.environment_overrides.find(_.environment == prod).get
    case _ =>
      error("Unknown Environment: " + environment)
  }

  /**
   * Adds all the other configurations to the application configuration.  Properties are
   * added to the application but do not over write them.
   */
  val runtimeConfig = defaultConfig + applicationConfig + pluginConfigs + environmentConfig

  lazy val webApplication:WebApp = {
    Class.forName(runtimeConfig.application_class, true, getClass.getClassLoader)
    	.getConstructor(classOf[Config])
			.newInstance(runtimeConfig)
			.asInstanceOf[WebApp]
  }

  def loadPlugin(name:String):Config = {
    new Config()
  }
}
