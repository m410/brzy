package org.brzy.plugin

import org.brzy.config.{Repository, Dependency, MergeConfig, Config}

/**
 *
 * @author Michael Fortin
 * @version $Id : $
 */
abstract class Plugin(map: Map[String, AnyRef]) extends Config(map) with MergeConfig[Plugin] {
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

  def asMap = {
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

}