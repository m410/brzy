package org.brzy.plugin

import org.brzy.config.{Repository, Dependency, MergeConfig, Config}

/**
 *
 * @author Michael Fortin
 * @version $Id : $
 */
abstract class Plugin(map: Map[String, AnyRef]) extends Config(map) with MergeConfig[Plugin] {
  val name: Option[String] = map.get("name").asInstanceOf[Option[String]].orElse(Option(null))
  val version: Option[String] = map.get("version").asInstanceOf[Option[String]].orElse(Option(null))
  val org: Option[String] = map.get("org").asInstanceOf[Option[String]].orElse(Option(null))
  val configClass: Option[String] = map.get("config_class").asInstanceOf[Option[String]].orElse(Option(null))
  val resourceClass: Option[String] = map.get("resource_class").asInstanceOf[Option[String]].orElse(Option(null))

  val remoteLocation: Option[String] = map.get("remote_location").asInstanceOf[Option[String]].orElse(Option(null))
  val localLocation: Option[String] = map.get("local_location").asInstanceOf[Option[String]].orElse(Option(null))

  val repositories: Option[List[Repository]] = map.get("repositories") match {
    case s: Some[List[Map[String, AnyRef]]] => Option(s.get.map(i => new Repository(i)).toList)
    case _ => Option(null)
  }
  val dependencies: Option[List[Dependency]] = map.get("dependencies") match {
    case s: Some[List[Map[String, AnyRef]]] => Option(s.get.map(i => new Dependency(i)).toList)
    case _ => Option(null)
  }

  def asMap = {
    Map[String, AnyRef](
      "name" -> name,
      "version" -> version,
      "org" -> org,
      "config_class" -> configClass,
      "resource_class" -> resourceClass,
      "remote_location" -> remoteLocation,
      "local_location" -> localLocation,
      "dependencies" -> {dependencies match {
        case s: Some[List[Dependency]] => Option(s.get.map(_.asMap).toList)
        case _ => Option(null)
      }},
      "repositories" -> {repositories match {
        case s: Some[List[Repository]] => Option(s.get.map(_.asMap).toList)
        case _ => Option(null)
      }})
  }

}