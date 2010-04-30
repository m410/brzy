package org.brzy.config

import reflect.BeanProperty
import collection.mutable.ArrayBuffer
import org.apache.commons.lang.builder.{CompareToBuilder, HashCodeBuilder, EqualsBuilder}

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class Logging extends MergeConfig[Logging]{
  @BeanProperty var provider:String = _
  @BeanProperty var appenders:Array[Appender] = _
  @BeanProperty var loggers:Array[Logger] = _
  @BeanProperty var root:Root = _


  def +(that: Logging) = {
    val log = new Logging

    if(that == null) {
      log.provider = provider
      log.appenders = appenders
      log.loggers = loggers
      log.root = root
    }
    else {
      log.provider = if(that.provider != null) that.provider else provider
      log.root = root + that.root

      if(that.appenders != null) {
        val appBuf = ArrayBuffer[Appender]()
        appenders.foreach(appender =>{
          if(that.appenders.contains(appender))
            appBuf += that.appenders(that.appenders.indexOf(appender)) + appender
          else
            appBuf += appender
        })
        log.appenders = appBuf.toArray
      }
      else {
       log.appenders = appenders
      }

      if(that.loggers != null) {
        val logBuf = ArrayBuffer[Logger]()
        loggers.foreach(logr =>{
          if(that.loggers.contains(logr))
            logBuf += that.loggers(that.loggers.indexOf(logr)) + logr
          else
            logBuf += logr
        })
        log.loggers = logBuf.toArray
      }
      else {
        log.loggers = loggers
      }
    }

    log
  }

  override def toString = new StringBuffer()
    .append("provider: ").append(provider)
    .append("appenders: ").append(if(appenders != null)appenders.mkString else "{}")
    .append("loggers: ").append(if(loggers != null) loggers.mkString else "{}")
    .append("root: ").append(root)
    .toString
}

/**
 *
 */
class Appender extends MergeConfig[Appender] with Comparable[Appender] {
  @BeanProperty var name:String = _
  @BeanProperty var appender_class:String = _
  @BeanProperty var layout:String = _
  @BeanProperty var pattern:String = _

  @BeanProperty var file:String = _
  @BeanProperty var rolling_policy:String = _
  @BeanProperty var file_name_pattern:String = _

  def compareTo(that: Appender) = {
    new CompareToBuilder()
      .append(this.name, that.name)
      .toComparison
  }

  def +(that: Appender) = {
    val app = new Appender
    app.name = if(that.name != null) that.name else name
    app.appender_class = if(that.appender_class != null) that.appender_class else appender_class
    app.layout = if(that.layout != null) that.layout else layout
    app.pattern = if(that.pattern != null) that.pattern else pattern
    app.file = if(that.file != null) that.file else file
    app.rolling_policy = if(that.rolling_policy != null) that.rolling_policy else rolling_policy
    app.file_name_pattern = if(that.file_name_pattern != null) that.file_name_pattern else file_name_pattern
    app
  }

  override def toString = new StringBuffer()
    .append("name: ").append(name).append(", ")
    .append("class: ").append(appender_class).append(", ")
    .append("layout: ").append(layout).append(", ")
    .append("pattern: ").append(pattern)
    .toString

  override def equals(obj: Any) = {
    if (obj == null)
      false
    else if(obj == this)
      true
    else {
      val rhs = obj.asInstanceOf[Appender]
      new EqualsBuilder()
        .append(name, rhs.name)
        .isEquals
    }
  }

  override def hashCode = {
    new HashCodeBuilder(17, 37)
      .append(name)
      .toHashCode
  }
}

/**
 *
 */
class Logger extends MergeConfig[Logger] with Comparable[Logger]{
  @BeanProperty var name:String =_
  @BeanProperty var level:String =_

  def compareTo(obj: Logger) = {
    new CompareToBuilder()
      .append(this.name, obj.name)
      .toComparison
  }

  def +(that: Logger) = {
    val logger = new Logger
    logger.name = if(that.name != null) that.name else name
    logger.level = if(that.level != null) that.level else level
    logger
  }

  override def toString = new StringBuffer()
    .append("name: ").append(name).append(", ")
    .append("level: ").append(level)
    .toString

  override def equals(obj: Any) = {
    if (obj == null)
      false
    else if(obj == this)
      true
    else {
      val rhs = obj.asInstanceOf[Appender]
      new EqualsBuilder()
        .append(name, rhs.name)
        .isEquals
    }
  }

  override def hashCode = {
    new HashCodeBuilder(11, 37)
      .append(name)
      .toHashCode
  }
}

/**
 *
 */
class Root extends MergeConfig[Root] {
  @BeanProperty var level:String =_
  @BeanProperty var ref:Array[String] =_

  def +(that: Root) = {
    val root = new Root
    root.ref = if(that.ref != null) that.ref else ref
    root.level = if(that.level != null) that.level else level
    root
  }

  override def toString = new StringBuffer()
    .append("level: ").append(level).append(", ")
    .append("ref: ").append(if(ref != null)ref.mkString else "{}")
    .toString
}