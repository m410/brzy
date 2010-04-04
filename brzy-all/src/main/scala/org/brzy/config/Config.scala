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
    new StringBuffer()
      .append(newline)
      .append("  environment").append("=").append(environment).append(newline)
      .append("  version").append("=").append(version).append(newline)
      .append("  name").append("=").append(name).append(newline)
      .append("  author").append("=").append(author).append(newline)
      .append("  description").append("=").append(description).append(newline)
      .append("  group_id").append("=").append(group_id).append(newline)
      .append("  artifact_id").append("=").append(artifact_id).append(newline)
      .append("  package_type").append("=").append(package_type).append(newline)
      .append("  src_package").append("=").append(src_package).append(newline)
      .append("  webapp_context").append("=").append(webapp_context).append(newline)
      .append("  test_framework").append("=").append(test_framework).append(newline)
      .append("  view_type").append("=").append(view_type).append(newline)
      .append("  scala_version").append("=").append(scala_version).append(newline)
      .append("  ant_version").append("=").append(ant_version).append(newline)
      .append("  ivy_version").append("=").append(ivy_version).append(newline)
      .append("  view_html_version").append("=").append(view_html_version).append(newline)
      .append("  persistence_type").append("=").append(persistence_type).append(newline)
      .append("  db_migration").append("=").append(db_migration).append(newline)
      .append("  db_generation").append("=").append(db_generation).append(newline)
      .append("  application_properties").append("=").append(application_properties).append(newline)
      .append("  application_class").append("=").append(application_class).append(newline)
      .append("  data_source").append("=").append(data_source).append(newline)
      .append("  repositories").append("=").append(repositories).append(newline)
      .append("  dependencies").append("=").append(dependencies).append(newline)
      .append("  logging").append("=").append(logging).append(newline)
      .append("  plugins").append("=").append(plugins).append(newline)
      .append("  environment_overrides").append("=").append(environment_overrides).append(newline)
      .append("  web_xml").append("=").append(web_xml)
      .append(newline)
      .toString
  }
}