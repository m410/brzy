package org.brzy.plugin


import java.util.{Map => JMap}
import collection.JavaConversions._

/**
 *
 * @author Michael Fortin
 * @version $Id : $
 */
class CascalPluginConfig(map: Map[String, AnyRef]) extends Plugin(map) {
  val configurationName = "Cascal"

  override def <<(that: Plugin):Plugin = {
    if (that == null) {
      this
    }
    else {
      new CascalPluginConfig(Map[String, AnyRef](
        "name" -> that.name.getOrElse(this.name.get),
        "version" -> that.version.getOrElse(this.version.get),
        "org" -> that.org.getOrElse(this.org.get),
        "config_class" -> that.configClass.getOrElse(this.configClass.get),
        "resource_class" -> that.resourceClass.getOrElse(this.resourceClass.get),

        "remote_location" -> that.remoteLocation.getOrElse(this.remoteLocation.get),
        "local_location" -> that.localLocation.getOrElse(this.localLocation.get),
        "repositories" -> {
          if (this.repositories.isDefined && that.repositories.isDefined)
            this.repositories.get.map(_.asMap).toList ++ that.repositories.get.map(_.asMap).toList
          else if (this.repositories.isDefined)
            this.repositories.get.map(_.asMap).toList
          else if (that.repositories.isDefined)
            that.repositories.get.map(_.asMap).toList
          else
            Option(null)
        },
        "dependencies" -> {
          if (this.dependencies.isDefined && that.dependencies.isDefined)
            this.dependencies.get.map(_.asMap).toList ++ that.dependencies.get.map(_.asMap).toList
          else if (this.dependencies.isDefined)
            this.dependencies.get.map(_.asMap).toList
          else if (that.dependencies.isDefined)
            that.dependencies.get.map(_.asMap).toList
          else
            Option(null)
        }
        ))
    }
  }

  override def asMap = super.asMap

}