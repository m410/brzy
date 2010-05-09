package org.brzy.config

import reflect.BeanProperty
import org.slf4j.LoggerFactory
import scala.collection.mutable.ArrayBuffer

/**
 * load default, load plugins, load app.
 * loading default and plugins has to ignore the about tag.
 *
 * implicit plugins:
 * logging, persistence
 *
 * @author Michael Fortin
 * @version $Id: $
 */
class AppConfig extends MergeConfig[AppConfig] {
	
  @BeanProperty var environment:String = _
  @BeanProperty var application:Application = _

  @BeanProperty var test_framework:String = _
  @BeanProperty var project:Project = _

  @BeanProperty var repositories:Array[Repository] = _
  @BeanProperty var dependencies:Array[Dependency] = _

  @BeanProperty var logging:Logging = _
  @BeanProperty var plugins:Array[PluginConfig] = _
  @BeanProperty var persistence:Array[PluginConfig] = _
  @BeanProperty var web_xml:java.util.ArrayList[java.util.HashMap[String,java.lang.Object]] = _
  @BeanProperty var views:Views = _

  @BeanProperty var environment_overrides:Array[AppConfig] = _

	/**
	 * merge this with other config, and return a new one
	 */
  def +(that:AppConfig) = {
    val config = copy

    if(that.environment != null)
      config.environment = that.environment

    if(application != null)
      config.application = application + that.application
    else // default config is always null
      config.application = that.application

    if(that.test_framework != null)
      config.test_framework = that.test_framework

    if(project != null)
      config.project = project + that.project

    if(logging != null)
      config.logging = logging + that.logging
    else// default config is always null
      config.logging = that.logging

    // webxml
    config.web_xml = new java.util.ArrayList[java.util.HashMap[String,AnyRef]]

    if(web_xml!= null)
      config.web_xml addAll web_xml

    if(that.web_xml!= null)
        config.web_xml addAll that.web_xml

    // dependencies
    val deps = ArrayBuffer[Dependency]()

    if(that.dependencies != null )
      deps ++= that.dependencies

    if(this.dependencies != null)
      deps ++= this.dependencies

    config.dependencies = deps.toArray

    // repositories
    val repos = ArrayBuffer[Repository]()

    if(that.repositories != null )
      repos ++= that.repositories

    if(this.repositories != null)
      repos ++= this.repositories

    config.repositories = repos.toArray

    // persistence
    val persist = ArrayBuffer[PluginConfig]()

    if(that.persistence != null )
      persist ++= that.persistence

    if(this.persistence != null)
      persist ++= this.persistence

    config.persistence = persist.toArray

    config
  }

  def ++(thosePlugins:Array[PluginConfig]) = {
    if(plugins == null) {
      plugins = thosePlugins
    }
    else {
      thosePlugins.foreach(thatPlugin =>
        if(plugins.exists(thisPlugin => thisPlugin.name == thatPlugin.name)) {
          var thisPlugin = plugins.find(thisPlugin => thisPlugin.name == thatPlugin.name).get
          thisPlugin = thisPlugin + thatPlugin
        }
        else {
          plugins = plugins :+ thatPlugin
        }
      )
    }

    plugins.foreach(p => {

      if(p.web_xml != null)
        this.web_xml.addAll(p.web_xml)

      if(p.dependencies != null)
        this.dependencies = this.dependencies ++ p.dependencies

      if(p.repositories != null)
        this.repositories = this.repositories ++ p.repositories
    })
    
    this
  }

  def copy = {
    val config = new AppConfig
    config.environment = environment
    config.application = application
    config.test_framework = test_framework
    config.project = project
    config.repositories = repositories
    config.dependencies = dependencies
    config.logging = logging
    config.plugins = plugins
    config.persistence = persistence
    config.web_xml = web_xml
    config.environment_overrides = environment_overrides
    config
  }

//  override def toString = {
//    val newline = System.getProperty("line.separator")
//    val sb = new StringBuilder()
//    sb.append(newline)
//    sb.append("  - environment").append("=").append(environment).append(newline)
//    sb.append("  - application").append("=").append(application).append(newline)
//    sb.append("  - project").append("=").append(project).append(newline)
//    sb.append("  - test_framework").append("=").append(test_framework).append(newline)
//    sb.append("  - views").append("=").append(views).append(newline)
//    sb.append("  - repositories").append("=").append(if(repositories != null)repositories.mkString else "").append(newline)
//    sb.append("  - dependencies").append("=").append(if(dependencies != null)dependencies.mkString else "").append(newline)
//    sb.append("  - logging").append("=").append(logging).append(newline)
//    sb.append("  - web_xml").append("=").append(web_xml)
//    sb.toString
//  }
}