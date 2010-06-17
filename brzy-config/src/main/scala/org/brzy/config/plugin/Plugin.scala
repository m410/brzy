package org.brzy.config.plugin

import org.brzy.config.common.{Config, MergeConfig, Repository, Dependency}
import org.apache.commons.lang.builder.{EqualsBuilder, HashCodeBuilder, CompareToBuilder}

/**
 *
 * @author Michael Fortin
 * @version $Id : $
 */
class Plugin(val map: Map[String, AnyRef]) extends Config(map) with MergeConfig[Plugin] with Ordered[Plugin] {
  val configurationName = "Plugin Reference"
  val name: Option[String] = map.get("name").asInstanceOf[Option[String]].orElse(None)
  val version: Option[String] = map.get("version").asInstanceOf[Option[String]].orElse(None)
  val org: Option[String] = map.get("org").asInstanceOf[Option[String]].orElse(None)
  val configClass: Option[String] = map.get("config_class").asInstanceOf[Option[String]].orElse(None)
  val resourceClass: Option[String] = map.get("resource_class").asInstanceOf[Option[String]].orElse(None)
  val remoteLocation: Option[String] = map.get("remote_location").asInstanceOf[Option[String]].orElse(None)
  val localLocation: Option[String] = map.get("local_location").asInstanceOf[Option[String]].orElse(None)


  val repositories: Option[List[Repository]] = map.get("repositories") match {
    case Some(s) =>
      if (s != null)
        Option(s.asInstanceOf[List[Map[String, AnyRef]]].map(i => new Repository(i)).toList)
      else
        None
    case _ => None
  }

  val dependencies: Option[List[Dependency]] = map.get("dependencies") match {
    case Some(s) =>
      if (s != null)
        Option(s.asInstanceOf[List[Map[String, AnyRef]]].map(i => new Dependency(i)).toList)
      else
        None
    case _ => None
  }

  override def asMap: Map[String, AnyRef] = map

  override def <<(that: Plugin) = {
    if (that == null) {
      this
    }
    else {
      new Plugin(Map[String, AnyRef](
        "name" -> that.name.getOrElse(null),
        "version" -> that.version.getOrElse(this.version.getOrElse(null)),
        "org" -> that.org.getOrElse(this.org.getOrElse(null)),
        "config_class" -> that.configClass.getOrElse(this.configClass.getOrElse(null)),
        "resource_class" -> that.resourceClass.getOrElse(this.resourceClass.getOrElse(null)),
        "remote_location" -> that.remoteLocation.getOrElse(this.remoteLocation.getOrElse(null)),
        "local_location" -> that.localLocation.getOrElse(this.localLocation.getOrElse(null)),
        "repositories" -> {
          if (this.repositories.isDefined && that.repositories.isDefined)
            this.repositories.get.map(_.asMap).toList ++ that.repositories.get.map(_.asMap).toList
          else if (this.repositories.isDefined)
            this.repositories.get.map(_.asMap).toList
          else if (that.repositories.isDefined)
            that.repositories.get.map(_.asMap).toList
          else
            null
        },
        "dependencies" -> {
          if (this.dependencies.isDefined && that.dependencies.isDefined)
            this.dependencies.get.map(_.asMap).toList ++ that.dependencies.get.map(_.asMap).toList
          else if (this.dependencies.isDefined)
            this.dependencies.get.map(_.asMap).toList
          else if (that.dependencies.isDefined)
            that.dependencies.get.map(_.asMap).toList
          else
            null
        }
        ))
    }
  }

  override def compare(that: Plugin) = {
    new CompareToBuilder()
            .append(this.name.getOrElse(null), that.name.getOrElse(null))
            .append(this.org.getOrElse(null), that.org.getOrElse(null))
            .append(this.version.getOrElse(null), that.version.getOrElse(null))
            .toComparison
  }


  override def equals(p1: Any) = {
    if (p1 == null)
      false
    else {
      val rhs = p1.asInstanceOf[Plugin]
      new EqualsBuilder()
              .appendSuper(super.equals(p1))
              .append(name.getOrElse(null), rhs.name.getOrElse(null))
              .append(org.getOrElse(null), rhs.org.getOrElse(null))
              .append(version.getOrElse(null), rhs.version.getOrElse(null))
              .isEquals
    }
  }

  override def hashCode = new HashCodeBuilder(11, 37)
          .append(name.getOrElse(null))
          .append(org.getOrElse(null))
          .append(version.getOrElse(null))
          .toHashCode


  override def toString = new StringBuilder()
          .append(configurationName)
          .append(": ")
          .append(name.getOrElse("?"))
          .append(", ")
          .append(org.getOrElse("?"))
          .append(", ")
          .append(version.getOrElse("?"))
          .toString
}