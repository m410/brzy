package org.brzy.plugin

import org.brzy.config.{Repository, Dependency, MergeConfig, Config}
import org.apache.commons.lang.builder.{CompareToBuilder, HashCodeBuilder, EqualsBuilder}

/**
 *
 * @author Michael Fortin
 * @version $Id : $
 */
abstract class Plugin(map: Map[String, AnyRef]) extends Config(map) with MergeConfig[Plugin] with Comparable[Plugin] {
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
      "name" -> name.getOrElse(null),
      "version" -> version.getOrElse(null),
      "org" -> org.getOrElse(null),
      "config_class" -> configClass.getOrElse(null),
      "resource_class" -> resourceClass.getOrElse(null),
      "remote_location" -> remoteLocation.getOrElse(null),
      "local_location" -> localLocation.getOrElse(null),
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


  override def compareTo(that: Plugin) = {
    new CompareToBuilder()
        .append(this.name.get, that.name.get)
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
              .isEquals
    }
  }

  override def hashCode = {
    new HashCodeBuilder(17, 37)
            .append(name.get)
            .toHashCode
  }
}