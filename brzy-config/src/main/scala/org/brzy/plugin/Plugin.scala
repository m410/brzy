package org.brzy.plugin

import org.brzy.config.{Repository, Dependency, MergeConfig, Config}

/**
 *
 * @author Michael Fortin
 * @version $Id : $
 */
abstract class Plugin[T](map: Map[String, AnyRef]) extends Config(map) with MergeConfig[T] {
  val name = set[String](map.get("name"))
  val version = set[String](map.get("version"))
  val org = set[String](map.get("org"))
  val configClass = set[String](map.get("config_class"))
  val resourceClass = set[String](map.get("resource_class"))

  val remoteLocation = set[String](map.get("remote_location"))
  val localLocation = set[String](map.get("local_location"))

  val dependencies = makeSeq[Dependency](classOf[Dependency], map.get("dependencies"))
  val repositories = makeSeq[Repository](classOf[Repository], map.get("repositories"))

  def asMap = {
    val map = new collection.mutable.HashMap[String, AnyRef]()

    if (name != null)
      map.put("name", name)

    if (version != null)
      map.put("version", version)

    if (org != null)
      map.put("org", org)

    if (configClass != null)
      map.put("config_class", configClass)

    if (resourceClass != null)
      map.put("resource_class", resourceClass)

    if (remoteLocation != null)
      map.put("remote_location", remoteLocation)

    if (localLocation != null)
      map.put("local_location", localLocation)

    if (dependencies != null)
      map.put("dependencies", dependencies.map(m=>m.asInstanceOf[Dependency].asMap))

    if (repositories != null)
      map.put("repositories", repositories.map(m=>m.asInstanceOf[Repository].asMap))
    
    Map[String, AnyRef]() ++ map
  }

  def +(that: Plugin[_]) = null
}