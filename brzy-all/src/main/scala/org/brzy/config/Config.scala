package org.brzy.config

import reflect.BeanProperty
import org.brzy.application.WebApp
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
class Config extends Merge[Config] {
	
	private val master = "master"
  @BeanProperty var environment:String = _
  @BeanProperty var config_type:String = _
  @BeanProperty var package_type:String = _

  @BeanProperty var version:String = _
  @BeanProperty var name:String = _
  @BeanProperty var author:String = _
  @BeanProperty var description:String = _
  @BeanProperty var group_id:String = _
  @BeanProperty var artifact_id:String = _

  @BeanProperty var webapp_context:String = _
  @BeanProperty var test_framework:String = _

  @BeanProperty var scala_version:String = _
  @BeanProperty var ant_version:String = _
  @BeanProperty var ivy_version:String = _

  @BeanProperty var views:Views = _

  @BeanProperty var application_properties:java.util.HashMap[String,String] = _
  @BeanProperty var application_class:String =_

  @BeanProperty var repositories:Array[Repository] = _
  @BeanProperty var dependencies:Array[Dependency] = _

  @BeanProperty var logging:Logging = _
  @BeanProperty var plugins:Array[Plugin] = _
  @BeanProperty var persistence:Array[Plugin] = _
  @BeanProperty var web_xml:Array[WebXmlNode] = _

  @BeanProperty var environment_overrides:Array[Config] = _

	/**
	 * merge this with other config, and return a new one
	 */
  def +(that:Config) = {
    val config = copy

		if(that.config_type == master) { // default merged to the app config, this needs to be update
	    config.version = that.version
	    config.name = that.name
	    config.author = that.author
	    config.description = that.description
	    config.group_id = that.group_id
	    config.artifact_id = that.artifact_id

	    config.webapp_context = that.webapp_context
	    config.test_framework = that.test_framework
	    config.scala_version = that.scala_version
	    config.ant_version = that.ant_version
	    config.ivy_version = that.ivy_version
	    config.application_class = that.application_class
		}
		
    config.application_properties = new java.util.HashMap[String,String]
    if(this.application_properties != null)
      config.application_properties.putAll(this.application_properties)
    if(that.application_properties != null)
      config.application_properties.putAll(that.application_properties)

    val deps = ArrayBuffer[Dependency]()
    if(that.dependencies != null )
      deps ++= that.dependencies
    if(this.dependencies != null)
      deps ++= this.dependencies
    config.dependencies = deps.toArray

    val repos = ArrayBuffer[Repository]()
    if(that.repositories != null )
      repos ++= that.repositories
    if(this.repositories != null)
      repos ++= this.repositories
    config.repositories = repos.toArray

    config
  }

  def +(config:Array[Config]) = {
    new Config()
  }

	def copy = {
		val c = new Config
		c.environment            =environment           
		c.config_type            =config_type           
		c.package_type           =package_type          
		c.version                =version               
		c.name                   =name                  
		c.author                 =author                
		c.description            =description           
		c.group_id               =group_id              
		c.artifact_id            =artifact_id           
		c.webapp_context         =webapp_context        
		c.test_framework         =test_framework        
		c.scala_version          =scala_version         
		c.ant_version            =ant_version           
		c.ivy_version            =ivy_version           
		c.views                   =views                  
		c.application_class      =application_class     
		c.logging                =logging               
		c.persistence            =persistence           
		c.web_xml                =web_xml               
		c
	}

  override def toString = {
    val newline = System.getProperty("line.separator")
    val sb = new StringBuilder()
    sb.append(newline)
    sb.append("  - environment").append("=").append(environment).append(newline)
    sb.append("  - version").append("=").append(version).append(newline)
    sb.append("  - name").append("=").append(name).append(newline)
    sb.append("  - author").append("=").append(author).append(newline)
    sb.append("  - description").append("=").append(description).append(newline)
    sb.append("  - group_id").append("=").append(group_id).append(newline)
    sb.append("  - artifact_id").append("=").append(artifact_id).append(newline)
    sb.append("  - package_type").append("=").append(package_type).append(newline)
    sb.append("  - webapp_context").append("=").append(webapp_context).append(newline)
    sb.append("  - test_framework").append("=").append(test_framework).append(newline)
    sb.append("  - views").append("=").append(views).append(newline)
    sb.append("  - scala_version").append("=").append(scala_version).append(newline)
    sb.append("  - ant_version").append("=").append(ant_version).append(newline)
    sb.append("  - ivy_version").append("=").append(ivy_version).append(newline)
    sb.append("  - application_properties").append("=").append(application_properties).append(newline)
    sb.append("  - application_class").append("=").append(application_class).append(newline)
    sb.append("  - repositories").append("=").append(if(repositories != null)repositories.mkString else "").append(newline)
    sb.append("  - dependencies").append("=").append(if(dependencies != null)dependencies.mkString else "").append(newline)
    sb.append("  - logging").append("=").append(logging).append(newline)
    sb.append("  - web_xml").append("=").append(web_xml)
    sb.toString
  }
}