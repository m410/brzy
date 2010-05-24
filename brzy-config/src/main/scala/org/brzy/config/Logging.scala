package org.brzy.config

import org.apache.commons.lang.builder.{CompareToBuilder, HashCodeBuilder, EqualsBuilder}
import java.util.{List => JList}
import collection.JavaConversions._

/**
 * @author Michael Fortin
 * @version $Id : $
 */
class Logging(m: Map[String, AnyRef]) extends Config(m) with MergeConfig[Logging] {
  val configurationName: String = "Logging"
  val provider: Option[String] = m.get("provider").asInstanceOf[Option[String]].orElse(None)
  val appenders: Option[List[Appender]] = m.get("appenders") match {
    case s: Some[List[Map[String, AnyRef]]] => Option(s.get.map(i => new Appender(i)).toList)
    case _ => None
  }
  val loggers: Option[List[Logger]] = m.get("loggers") match {
    case s: Some[List[Map[String, AnyRef]]] => Option(s.get.map(i => new Logger(i)).toList)
    case _ => None
  }
  val root: Option[Root] = m.get("root") match {
    case s: Some[Map[String, AnyRef]] => Option(new Root(s.get))
    case _ => None
  }


  def asMap = {
    Map[String, AnyRef](
      "provider" -> provider.getOrElse(null),
      "appenders" -> appenders.get.map(_.asMap).toList,
      "loggers" -> loggers.get.map(_.asMap).toList,
      "root" -> root.get.asMap
      )
  }

  def <<(that: Logging) = {
    if (that == null) {
      this
    }
    else {
      new Logging(Map[String, AnyRef](
        "provider" -> that.provider.getOrElse(this.provider.get),
        "appenders" -> {
          if (this.appenders.isDefined && that.appenders.isDefined)
            {this.appenders.get ++ that.appenders.get}.map(_.asMap).toList
          else if (this.appenders.isDefined)
            this.appenders.get.map(_.asMap).toList
          else if (that.appenders.isDefined)
            that.appenders.get.map(_.asMap).toList
          else
            null
        },
        "loggers" -> {
          if (this.loggers.isDefined && that.loggers.isDefined)
            this.loggers.get.map(_.asMap).toList ++ that.loggers.get.map(_.asMap).toList
          else if (this.loggers.isDefined)
            this.loggers.get.map(_.asMap).toList
          else if (that.loggers.isDefined)
            that.loggers.get.map(_.asMap).toList
          else
            null
        },
        "root" -> {this.root.get << that.root.get}.asMap
        ))
    }
  }
}

/**
 *
 */
class Appender(m: Map[String, AnyRef]) extends Config(m) with MergeConfig[Appender] with Comparable[Appender] {
  val configurationName = "Appender"
  val name: Option[String] = m.get("name").asInstanceOf[Option[String]].orElse(None)
  val appenderClass: Option[String] = m.get("appender_class").asInstanceOf[Option[String]].orElse(None)
  val layout: Option[String] = m.get("layout").asInstanceOf[Option[String]].orElse(None)
  val pattern: Option[String] = m.get("pattern").asInstanceOf[Option[String]].orElse(None)

  val file: Option[String] = m.get("file").asInstanceOf[Option[String]].orElse(None)
  val rollingPolicy: Option[String] = m.get("rolling_policy").asInstanceOf[Option[String]].orElse(None)
  val fileNamePattern: Option[String] = m.get("file_name_pattern").asInstanceOf[Option[String]].orElse(None)


  def asMap = {
    Map[String, AnyRef](
      "name" -> name.getOrElse(null),
      "appender_class" -> appenderClass.getOrElse(null),
      "layout" -> layout.getOrElse(null),
      "pattern" -> pattern.getOrElse(null),
      "file" -> file.getOrElse(null),
      "rolling_policy" -> rollingPolicy.getOrElse(null),
      "file_name_pattern" -> fileNamePattern.getOrElse(null))
  }

  def <<(that: Appender) = {
    if (that == null)
      this
    else
      new Appender(Map[String, String](
        "name" -> that.name.getOrElse(this.name.get),
        "appender_class" -> that.appenderClass.getOrElse(this.appenderClass.get),
        "layout" -> that.layout.getOrElse(this.layout.get),
        "pattern" -> that.pattern.getOrElse(this.pattern.get),
        "file" -> that.file.getOrElse(this.file.get),
        "rolling_policy" -> that.rollingPolicy.getOrElse(this.rollingPolicy.get),
        "file_name_pattern" -> that.fileNamePattern.getOrElse(this.fileNamePattern.get)
        ))
  }

  override def equals(obj: Any) = {
    if (obj == null)
      false
    else if (obj == this)
      true
    else {
      val rhs = obj.asInstanceOf[Appender]
      new EqualsBuilder()
              .append(name, rhs.name)
              .isEquals
    }
  }

  def compareTo(that: Appender) = {
    new CompareToBuilder()
            .append(this.name, that.name)
            .toComparison
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
class Logger(m: Map[String, AnyRef]) extends Config(m) with MergeConfig[Logger] with Comparable[Logger] {
  val configurationName = "Logger"
  val name: Option[String] = m.get("name").asInstanceOf[Option[String]].orElse(None)
  val level: Option[String] = m.get("level").asInstanceOf[Option[String]].orElse(None)

  def asMap = {
    Map[String, AnyRef](
      "name" -> name.getOrElse(null),
      "level" -> level.getOrElse(null))
  }

  def <<(that: Logger) = {
    if (that == null)
      this
    else
      new Logger(Map[String, String](
        "name" -> that.name.getOrElse(this.name.get),
        "level" -> that.level.getOrElse(this.level.get)
        ))
  }

  def compareTo(obj: Logger) = {
    new CompareToBuilder()
            .append(this.name, obj.name)
            .toComparison
  }

  override def equals(obj: Any) = {
    if (obj == null)
      false
    else if (obj == this)
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
class Root(m: Map[String, AnyRef]) extends Config(m) with MergeConfig[Root] {
  val configurationName = "Root"
  val level: Option[String] = m.get("level").asInstanceOf[Option[String]].orElse(None)
  val ref: Option[List[String]] = m.get("ref") match {
    case s: Some[List[String]] => s
    case _ => None
  }


  def asMap = {
    Map[String, AnyRef](
      "level" -> level.getOrElse(null),
      "ref" -> ref.getOrElse(null)
      )
  }

  def <<(that: Root) = {
    if (that == null)
      this
    else
      new Root(Map[String, AnyRef](
        "level" -> that.level.getOrElse(this.level.get),
        "ref" -> {
          if (this.ref.isDefined && that.ref.isDefined)
            this.ref.get ++ that.ref.get
          else if (this.ref.isDefined)
            this.ref.get
          else if (that.ref.isDefined)
            that.ref.get
          else
            null
        }))
  }
}