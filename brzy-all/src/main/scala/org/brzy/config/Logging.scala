package org.brzy.config

import reflect.BeanProperty

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class Logging {
  @BeanProperty var provider:String = _
  @BeanProperty var appenders:Array[Appender] = _
  @BeanProperty var loggers:Array[Logger] = _
  @BeanProperty var root:Root = _

  override def toString = new StringBuffer()
    .append("provider: ").append(provider)
    .append("appenders: ").append(if(appenders != null)appenders.mkString else "{}")
    .append("loggers: ").append(if(loggers != null) loggers.mkString else "{}")
    .append("root: ").append(root)
    .toString
}

class Appender {
  @BeanProperty var name:String = _
  @BeanProperty var appender_class:String = _
  @BeanProperty var layout:String = _
  @BeanProperty var pattern:String = _

  @BeanProperty var file:String = _
  @BeanProperty var rolling_policy:String = _
  @BeanProperty var file_name_pattern:String = _

  override def toString = new StringBuffer()
    .append("name: ").append(name).append(", ")
    .append("class: ").append(appender_class).append(", ")
    .append("layout: ").append(layout).append(", ")
    .append("pattern: ").append(pattern)
    .toString
}

class Logger {
  @BeanProperty var name:String =_
  @BeanProperty var level:String =_

  override def toString = new StringBuffer()
    .append("name: ").append(name).append(", ")
    .append("level: ").append(level)
    .toString
}

class Root {
  @BeanProperty var level:String =_
  @BeanProperty var ref:Array[String] =_

  override def toString = new StringBuffer()
    .append("level: ").append(level).append(", ")
    .append("ref: ").append(if(ref != null)ref.mkString else "{}")
    .toString
}