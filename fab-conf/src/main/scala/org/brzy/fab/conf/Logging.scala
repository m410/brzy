/*
 * Copyright 2010 Michael Fortin <mike@brzy.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.brzy.fab.conf

import org.apache.commons.lang.builder.{CompareToBuilder, HashCodeBuilder, EqualsBuilder}

/**
 * Creates the logging entry from the brzy-webapp.b.yml configuration file.  This
 * is currently wired to Logback, but could later be tooled to accept log4j or JUL.
 *
 * @author Michael Fortin
 */
class Logging(m: Map[String, AnyRef]) extends Merge[Logging] {

  val provider: Option[String] = m.get("provider").asInstanceOf[Option[String]].orElse(None)
  val appenders: Option[List[Appender]] = m.get("appenders") match {
    case Some(s) => Option(s.asInstanceOf[List[Map[String, AnyRef]]].map(i => new Appender(i)).toList)
    case _ => None
  }
  val loggers: Option[List[Logger]] = m.get("loggers") match {
    case Some(s) => Option(s.asInstanceOf[List[Map[String, AnyRef]]].map(i => new Logger(i)).toList)
    case _ => None
  }
  val root: Option[Root] = m.get("root") match {
    case Some(s) => Option(new Root(s.asInstanceOf[Map[String, AnyRef]]))
    case _ => None
  }


  def asMap = m

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
 *  An appender for logging output.
 */
class Appender(m: Map[String, AnyRef]) extends Merge[Appender] with Ordered[Appender] {
  val configurationName = "Appender"
  val name: Option[String] = m.get("name").asInstanceOf[Option[String]].orElse(None)
  val appenderClass: Option[String] = m.get("appender_class").asInstanceOf[Option[String]].orElse(None)
  val layout: Option[String] = m.get("layout").asInstanceOf[Option[String]].orElse(None)
  val pattern: Option[String] = m.get("pattern").asInstanceOf[Option[String]].orElse(None)

  val file: Option[String] = m.get("file").asInstanceOf[Option[String]].orElse(None)
  val rollingPolicy: Option[String] = m.get("rolling_policy").asInstanceOf[Option[String]].orElse(None)
  val fileNamePattern: Option[String] = m.get("file_name_pattern").asInstanceOf[Option[String]].orElse(None)


  def asMap = m

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

  override def compare(that: Appender) = {
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
 *  An individual Logger (or category) used by the logging api.
 */
class Logger(m: Map[String, AnyRef]) extends Merge[Logger] with Ordered[Logger] {
  val configurationName = "Logger"
  val name: Option[String] = m.get("name").asInstanceOf[Option[String]].orElse(None)
  val level: Option[String] = m.get("level").asInstanceOf[Option[String]].orElse(None)

  def asMap = m

  def <<(that: Logger) = {
    if (that == null)
      this
    else
      new Logger(Map[String, String](
        "name" -> that.name.getOrElse(this.name.get),
        "level" -> that.level.getOrElse(this.level.get)
        ))
  }

  override def compare(obj: Logger) = {
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
 * The root logger
 */
class Root(m: Map[String, AnyRef]) extends Merge[Root] {
  val configurationName = "Root"
  val level: Option[String] = m.get("level").asInstanceOf[Option[String]].orElse(None)
  val ref: Option[List[String]] = m.get("ref") match {
    case Some(s) => Option(s.asInstanceOf[List[String]])
    case _ => None
  }


  def asMap = m

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