package org.brzy.squeryl

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
    else if(that.isInstanceOf[SquerylPluginConfig]) {
      val it = that.asInstanceOf[SquerylPluginConfig]
      new SquerylPluginConfig(Map[String, AnyRef](
        "driver" -> it.driver.getOrElse(this.driver.getOrElse(null)),
        "url" -> it.url.getOrElse(this.url.getOrElse(null)),
        "user_name" -> it.userName.getOrElse(this.userName.getOrElse(null)),
        "password" -> it.password.getOrElse(this.password.getOrElse(null)),
        "adaptor_name" -> it.adaptorName.getOrElse(this.adaptorName.getOrElse(null)),

        "name" -> it.name.getOrElse(this.name.getOrElse(null)),
        "version" -> it.version.getOrElse(this.version.getOrElse(null)),
        "org" -> it.org.getOrElse(this.org.getOrElse(null)),
        "config_class" -> it.configClass.getOrElse(this.configClass.getOrElse(null)),
        "resource_class" -> it.resourceClass.getOrElse(this.resourceClass.getOrElse(null)),
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
    else {
      new SquerylPluginConfig(Map[String, AnyRef](
        "driver" -> that.map.get("driver").getOrElse(this.driver.getOrElse(null)),
        "url" -> that.map.get("url").getOrElse(this.url.getOrElse(null)),
        "user_name" -> that.map.get("user_name").getOrElse(this.userName.getOrElse(null)),
        "password" -> that.map.get("password").getOrElse(this.password.getOrElse(null)),
        "adaptor_name" -> that.map.get("adapter_name").getOrElse(this.adaptorName.getOrElse(null)),

        "name" -> that.name.getOrElse(this.name.getOrElse(null)),
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

  override def asMap:Map[String,AnyRef] = map
}