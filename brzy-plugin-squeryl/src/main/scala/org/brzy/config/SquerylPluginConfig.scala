package org.brzy.plugin

import org.brzy.config.plugin.Plugin

/**
 * @author Michael Fortin
 * @version $Id : $
 */
class SquerylPluginConfig(map: Map[String, AnyRef]) extends Plugin(map) {
  override val configurationName = "Squeryl"

  val driver: Option[String] = map.get("driver").asInstanceOf[Option[String]].orElse(None)
  val url: Option[String] = map.get("url").asInstanceOf[Option[String]].orElse(None)
  val userName: Option[String] = map.get("user_name").asInstanceOf[Option[String]].orElse(None)
  val password: Option[String] = map.get("password").asInstanceOf[Option[String]].orElse(None)
  val adaptorName: Option[String] = map.get("adaptor_name").asInstanceOf[Option[String]].orElse(None)


  override def <<(that: Plugin):Plugin  = {
    if (that == null) {
      this
    }
    else {
      val it = that.asInstanceOf[SquerylPluginConfig]
      new SquerylPluginConfig(Map[String, AnyRef](
        "name" -> it.name.getOrElse(this.name.getOrElse(null)),
        "version" -> it.version.getOrElse(this.version.getOrElse(null)),
        "org" -> it.org.getOrElse(this.org.getOrElse(null)),
        "config_class" -> it.configClass.getOrElse(this.configClass.getOrElse(null)),
        "resource_class" -> it.resourceClass.getOrElse(this.resourceClass.getOrElse(null)),
        "driver" -> it.driver.getOrElse(this.driver.getOrElse(null)),
        "url" -> it.url.getOrElse(this.url.getOrElse(null)),
        "userName" -> it.userName.getOrElse(this.userName.getOrElse(null)),
        "password" -> it.password.getOrElse(this.password.getOrElse(null)),
        "adaptorName" -> it.adaptorName.getOrElse(this.adaptorName.getOrElse(null)),
        "remote_location" -> it.remoteLocation.getOrElse(this.remoteLocation.getOrElse(null)),
        "local_location" -> it.localLocation.getOrElse(this.localLocation.getOrElse(null)),
        "repositories" -> {
          if (this.repositories.isDefined && it.repositories.isDefined)
            this.repositories.get.map(_.asMap).toList ++ it.repositories.get.map(_.asMap).toList
          else if (this.repositories.isDefined)
            this.repositories.get.map(_.asMap).toList
          else if (it.repositories.isDefined)
            it.repositories.get.map(_.asMap).toList
          else
            null
        },
        "dependencies" -> {
          if (this.dependencies.isDefined && it.dependencies.isDefined)
            this.dependencies.get.map(_.asMap).toList ++ it.dependencies.get.map(_.asMap).toList
          else if (this.dependencies.isDefined)
            this.dependencies.get.map(_.asMap).toList
          else if (it.dependencies.isDefined)
            it.dependencies.get.map(_.asMap).toList
          else
            null
        }
        ))
    }
  }

  override def asMap:Map[String,AnyRef] = {
    super.asMap ++ Map[String, AnyRef](
      "driver" -> driver.getOrElse(null),
      "url" -> url.getOrElse(null),
      "user_name" -> userName.getOrElse(null),
      "password" -> password.getOrElse(null),
      "adaptor_name" -> adaptorName.getOrElse(null))
  }
}