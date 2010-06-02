package org.brzy.plugin

import org.brzy.config.plugin.Plugin

/**
 * @author Michael Fortin
 * @version $Id : $
 */
class ScalatePluginConfig(map: Map[String, AnyRef]) extends Plugin(map) {
  override val configurationName = "Scalate"
  val fileExtension: Option[String] = map.get("file_extension").asInstanceOf[Option[String]].orElse(None)
  val webXml: Option[List[Map[String, AnyRef]]] = map.get("web_xml").asInstanceOf[Option[List[Map[String, AnyRef]]]].orElse(None)

  override def <<(that: Plugin):Plugin  = {
    if (that == null) {
      this
    }
    else {
      val it = that.asInstanceOf[ScalatePluginConfig]
      new ScalatePluginConfig(Map[String, AnyRef](
        "name" -> it.name.getOrElse(this.name.getOrElse(null)),
        "version" -> it.version.getOrElse(this.version.getOrElse(null)),
        "org" -> it.org.getOrElse(this.org.getOrElse(null)),
        "config_class" -> it.configClass.getOrElse(this.configClass.getOrElse(null)),
        "resource_class" -> it.resourceClass.getOrElse(this.resourceClass.getOrElse(null)),
        "file_extension" -> it.fileExtension.getOrElse(this.fileExtension.getOrElse(null)),

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
        },
        "web_xml" -> {
          if (this.webXml.isDefined && it.webXml.isDefined && this.webXml.get != null && it.webXml.get != null)
            this.webXml.get ++ it.webXml.get
          else if (this.webXml.isDefined)
            this.webXml.get
          else if (it.webXml.isDefined)
            it.webXml.get
          else
            null
        }))
    }
  }

  override def asMap:Map[String,AnyRef] = {
    super.asMap ++ Map[String, AnyRef](
      "file_extension" -> fileExtension.getOrElse(null),
      "web_xml" -> webXml.getOrElse(null)
      )
  }

}