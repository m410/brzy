package org.brzy.plugin

import org.brzy.config.{Config, MergeConfig, Repository, Dependency}
import org.apache.commons.lang.builder.{EqualsBuilder, HashCodeBuilder, CompareToBuilder}

/**
 *
 * @author Michael Fortin
 * @version $Id : $
 */
class Plugin(map: Map[String, AnyRef]) extends Config(map) with MergeConfig[Plugin] with Comparable[Plugin] {

  val configurationName = "Plugin Reference"
  val name: Option[String] = map.get("name").asInstanceOf[Option[String]].orElse(None)
  val version: Option[String] = map.get("version").asInstanceOf[Option[String]].orElse(None)
  val org: Option[String] = map.get("org").asInstanceOf[Option[String]].orElse(None)
  val configClass: Option[String] = map.get("config_class").asInstanceOf[Option[String]].orElse(None)
  val resourceClass: Option[String] = map.get("resource_class").asInstanceOf[Option[String]].orElse(None)
  val remoteLocation: Option[String] = map.get("remote_location").asInstanceOf[Option[String]].orElse(None)
  val localLocation: Option[String] = map.get("local_location").asInstanceOf[Option[String]].orElse(None)
  

  val repositories: Option[List[Repository]] = map.get("repositories") match {
    case s: Some[List[Map[String, AnyRef]]] =>
      if (s.get != null)
        Option(s.get.map(i => new Repository(i)).toList)
      else
        None
    case _ => None
  }

  val dependencies: Option[List[Dependency]] = map.get("dependencies") match {
    case s: Some[List[Map[String, AnyRef]]] =>
      if (s.get != null)
        Option(s.get.map(i => new Dependency(i)).toList)
      else
        None
    case _ => None
  }

  override def asMap:Map[String,AnyRef] = {
    Map[String, AnyRef](
      "dependencies" -> {
        dependencies match {
          case s: Some[List[Dependency]] => s.get.map(_.asMap).toList
          case _ => List[Map[String, AnyRef]]()
        }
      },
      "repositories" -> {
        repositories match {
          case s: Some[List[Repository]] => s.get.map(_.asMap).toList
          case _ => List[Map[String, AnyRef]]()
        }
      })
  }


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

  def compareTo(that: Plugin) = {
    new CompareToBuilder()
        .append(this.name.get, that.name.get)
        .append(this.org.get, that.org.get)
        .append(this.version.get, that.version.get)
        .toComparison
  }


    override def equals(p1: Any) = {
    if (p1 == null)
      false
    else {
      val rhs = p1.asInstanceOf[Plugin]
      new EqualsBuilder()
          .appendSuper(super.equals(p1))
          .append(name.get, rhs.name.get)
          .append(org.get, rhs.org.get)
          .append(version.get, rhs.version.get)
          .isEquals
    }
  }

  override def hashCode =  new HashCodeBuilder(11, 37)
      .append(name.get)
      .append(org.get)
      .append(version.get)
      .toHashCode


  override def toString = "Plugin: " + name.get
}