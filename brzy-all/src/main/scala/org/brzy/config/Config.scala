package org.brzy.config

import reflect.BeanProperty
import org.brzy.application.WebApp
import org.slf4j.LoggerFactory

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class Config extends Merge[Config] {
  @BeanProperty var environment:String = _
  @BeanProperty var version:String = _
  @BeanProperty var name:String = _
  @BeanProperty var author:String = _
  @BeanProperty var description:String = _
  @BeanProperty var group_id:String = _
  @BeanProperty var artifact_id:String = _
  @BeanProperty var package_type:String = _
  @BeanProperty var src_package:String = _
  @BeanProperty var webapp_context:String = _
  @BeanProperty var test_framework:String = _
  @BeanProperty var view_type:String = _
  @BeanProperty var scala_version:String = _
  @BeanProperty var ant_version:String = _
  @BeanProperty var ivy_version:String = _
  @BeanProperty var view_html_version:String = _
  @BeanProperty var persistence_type:String = "jpa"
  @BeanProperty var persistence_properties:java.util.HashMap[String,String]= _
  @BeanProperty var db_migration:Boolean = _
  @BeanProperty var db_generation:Boolean = _
  @BeanProperty var application_properties:java.util.HashMap[String,String] = _
  @BeanProperty var application_class:String =_

  @BeanProperty var data_source:DataSource =_
  @BeanProperty var repositories:Array[Repository] = _
  @BeanProperty var dependencies:Array[Dependency] = _
  @BeanProperty var logging:Logging = _
  @BeanProperty var plugins:Array[Plugin] = _
  @BeanProperty var environment_overrides:Array[Config] = _
  @BeanProperty var web_xml:Array[WebXmlNode] = _

  def merge(that:Config):Config = {
    if(that.environment != null) this.environment = that.environment
    if(that.version != null) this.version = that.version
    if(that.name != null) this.name = that.name
    if(that.author != null) this.author = that.author
    if(that.description != null) this.description = that.description
    if(that.group_id != null) this.group_id = that.group_id
    if(that.artifact_id != null) this.artifact_id = that.artifact_id
    if(that.package_type != null) this.package_type = that.package_type
    if(that.src_package != null) this.src_package = that.src_package
    if(that.webapp_context != null) this.webapp_context = that.webapp_context
    if(that.test_framework != null) this.test_framework = that.test_framework
    if(that.view_type != null) this.view_type = that.view_type
    if(that.scala_version != null) this.scala_version = that.scala_version
    if(that.ant_version != null) this.ant_version = that.ant_version
    if(that.ivy_version != null) this.ivy_version = that.ivy_version
    if(that.view_html_version != null) this.view_html_version = that.view_html_version
    if(that.persistence_type != null) this.persistence_type = that.persistence_type

    if(that.db_migration ) this.db_migration = true
    if(that.db_generation) this.db_generation = true

    if(that.application_properties != null && this.application_properties == null)
      this.application_properties = that.application_properties
    else if(that.application_properties != null && this.application_properties != null)
      this.application_properties.putAll(that.application_properties)

    if(that.persistence_properties != null && this.persistence_properties == null)
      this.persistence_properties = that.persistence_properties
    else if(that.persistence_properties != null && this.persistence_properties != null)
      this.persistence_properties.putAll(that.persistence_properties)

    if(that.plugins != null && this.plugins == null)
      this.plugins = that.plugins
    else if(that.plugins != null && this.plugins != null)
      this.plugins = this.plugins ++ that.plugins

    if(that.dependencies != null && this.dependencies == null)
      this.dependencies = that.dependencies
    else if(that.dependencies != null && this.dependencies != null)
      this.dependencies = this.dependencies ++ that.dependencies

    if(that.repositories != null && this.repositories == null)
      this.repositories = that.repositories
    else if(that.repositories != null && this.repositories != null)
      this.repositories = this.repositories ++ that.repositories

    if(that.logging != null) this.logging = that.logging

    if(that.application_class != null) this.application_class = that.application_class


    this
  }

  def initApp():WebApp = {
    val log = LoggerFactory.getLogger(getClass)
    log.info(this.toString)
    log.info("Starting with Application class: {}", application_class)
    val clazz = Class.forName(application_class, true, getClass.getClassLoader)
    clazz.getConstructor(classOf[Config]).newInstance(this).asInstanceOf[WebApp]
  }

  override def toString = {
    val newline = System.getProperty("line.separator")
    val sb = new StringBuffer()
    sb.append(newline)
    sb.append("  - environment").append("=").append(environment).append(newline)
    sb.append("  - version").append("=").append(version).append(newline)
    sb.append("  - name").append("=").append(name).append(newline)
    sb.append("  - author").append("=").append(author).append(newline)
    sb.append("  - description").append("=").append(description).append(newline)
    sb.append("  - group_id").append("=").append(group_id).append(newline)
    sb.append("  - artifact_id").append("=").append(artifact_id).append(newline)
    sb.append("  - package_type").append("=").append(package_type).append(newline)
    sb.append("  - src_package").append("=").append(src_package).append(newline)
    sb.append("  - webapp_context").append("=").append(webapp_context).append(newline)
    sb.append("  - test_framework").append("=").append(test_framework).append(newline)
    sb.append("  - view_type").append("=").append(view_type).append(newline)
    sb.append("  - scala_version").append("=").append(scala_version).append(newline)
    sb.append("  - ant_version").append("=").append(ant_version).append(newline)
    sb.append("  - ivy_version").append("=").append(ivy_version).append(newline)
    sb.append("  - view_html_version").append("=").append(view_html_version).append(newline)
    sb.append("  - persistence_type").append("=").append(persistence_type).append(newline)
    sb.append("  - db_migration").append("=").append(db_migration).append(newline)
    sb.append("  - db_generation").append("=").append(db_generation).append(newline)
    sb.append("  - application_properties").append("=").append(application_properties).append(newline)
    sb.append("  - persistence_properties").append("=").append(persistence_properties).append(newline)
    sb.append("  - application_class").append("=").append(application_class).append(newline)
    sb.append("  - data_source").append("=").append(data_source).append(newline)
    sb.append("  - repositories").append("=").append(if(repositories != null)repositories.mkString else "").append(newline)
    sb.append("  - dependencies").append("=").append(if(dependencies != null)dependencies.mkString else "").append(newline)
    sb.append("  - logging").append("=").append(logging).append(newline)
    sb.append("  - web_xml").append("=").append(web_xml)
    sb.toString
  }
}