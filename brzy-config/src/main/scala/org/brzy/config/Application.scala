package org.brzy.config

import reflect.BeanProperty

/**
 * TODO rename this to author?
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
class Application extends MergeConfig[Application] {
  @BeanProperty var version:String = _
  @BeanProperty var name:String = _
  @BeanProperty var author:String = _
  @BeanProperty var description:String = _
  @BeanProperty var group_id:String = _
  @BeanProperty var artifact_id:String = _
  @BeanProperty var properties:java.util.HashMap[String,String] = _
  @BeanProperty var application_class:String =_
  @BeanProperty var webapp_context:String = _


  def +(that: Application) = {
    val app = new Application

    if(that != null) {
      app.version = if(that.version != null) that.version else version
      app.name = if(that.name != null) that.name else name
      app.author = if(that.author != null) that.author else author
      app.description = if(that.description != null) that.description else description
      app.group_id = if(that.group_id != null) that.group_id else group_id
      app.artifact_id = if(that.artifact_id != null) that.artifact_id else artifact_id
      app.properties = if(that.properties != null) that.properties else properties
      app.application_class = if(that.application_class != null) that.application_class else application_class
      app.webapp_context = if(that.webapp_context != null) that.webapp_context else webapp_context
      app.properties = new java.util.HashMap[String,String]

      if(properties != null)
        app.properties.putAll(properties)

      if(that.properties != null)
        app.properties.putAll(that.properties)
    }
    else {
      app.version = version
      app.name = name
      app.author = author
      app.description = description
      app.group_id = group_id
      app.artifact_id = artifact_id
      app.properties = properties
      app.application_class = application_class
      app.webapp_context = webapp_context
      app.properties = new java.util.HashMap[String,String]

      if(properties != null)
        app.properties.putAll(properties)
    }
    app
  }

  override def toString = {
    val newline = System.getProperty("line.separator")
    val sb = new StringBuilder()
    sb.append(newline)
    sb.append("  - application").append(newline)
    sb.append("   - version")                .append("=").append(version).append(newline)
    sb.append("   - name")                   .append("=").append(name).append(newline)
    sb.append("   - author")                 .append("=").append(author).append(newline)
    sb.append("   - description")            .append("=").append(description).append(newline)
    sb.append("   - group_id")               .append("=").append(group_id).append(newline)
    sb.append("   - artifact_id")            .append("=").append(artifact_id).append(newline)
    sb.append("   - properties")             .append("=").append(properties).append(newline)
    sb.append("   - application_class")      .append("=").append(application_class).append(newline)
    sb.append("   - webapp_context")         .append("=").append(webapp_context).append(newline)
    sb.toString
  }
}