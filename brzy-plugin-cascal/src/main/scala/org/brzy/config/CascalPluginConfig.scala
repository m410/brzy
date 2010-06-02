package org.brzy.config


import org.brzy.config.plugin.Plugin

/**
 *
 * @author Michael Fortin
 * @version $Id : $
 */
class CascalPluginConfig(map: Map[String, AnyRef]) extends Plugin(map) {
  override val configurationName = "Cascal"

  override def <<(that: Plugin):Plugin = {
    if (that == null) {
      this
    }
    else {
      new CascalPluginConfig(Map[String, AnyRef](
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

  override def asMap = super.asMap

}