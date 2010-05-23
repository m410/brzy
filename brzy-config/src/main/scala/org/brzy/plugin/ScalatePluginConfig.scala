package org.brzy.plugin

/**
 * @author Michael Fortin
 * @version $Id : $
 */
class ScalatePluginConfig(map: Map[String, AnyRef]) extends Plugin(map) {
  val configurationName = "Scalate"
  val fileExtension: Option[String] = map.get("file_extension").asInstanceOf[Option[String]].orElse(Option(null))
  val webXml: Option[List[Map[String, AnyRef]]] = map.get("web_xml").asInstanceOf[Option[List[Map[String, AnyRef]]]].orElse(Option(null))

  override def <<(that: Plugin):Plugin  = {
    if (that == null) {
      this
    }
    else {
      val it = that.asInstanceOf[ScalatePluginConfig]
      new ScalatePluginConfig(Map[String, AnyRef](
        "name" -> it.name.getOrElse(this.name.get),
        "version" -> it.version.getOrElse(this.version.get),
        "org" -> it.org.getOrElse(this.org.get),
        "config_class" -> it.configClass.getOrElse(this.configClass.get),
        "resource_class" -> it.resourceClass.getOrElse(this.resourceClass.get),
        "file_extension" -> it.fileExtension.getOrElse(this.fileExtension.get),

        "remote_location" -> it.remoteLocation.getOrElse(this.remoteLocation.get),
        "local_location" -> it.localLocation.getOrElse(this.localLocation.get),
        "repositories" -> {
          if (this.repositories.isDefined && it.repositories.isDefined)
            this.repositories.get.map(_.asMap).toList ++ it.repositories.get.map(_.asMap).toList
          else if (this.repositories.isDefined)
            this.repositories.get.map(_.asMap).toList
          else if (it.repositories.isDefined)
            it.repositories.get.map(_.asMap).toList
          else
            Option(null)
        },
        "dependencies" -> {
          if (this.dependencies.isDefined && it.dependencies.isDefined)
            this.dependencies.get.map(_.asMap).toList ++ it.dependencies.get.map(_.asMap).toList
          else if (this.dependencies.isDefined)
            this.dependencies.get.map(_.asMap).toList
          else if (it.dependencies.isDefined)
            it.dependencies.get.map(_.asMap).toList
          else
            Option(null)
        },
        "web_xml" -> {
          if (this.webXml.isDefined && it.webXml.isDefined)
            this.webXml.get ++ it.webXml.get
          else if (this.webXml.isDefined)
            this.webXml.get
          else if (it.webXml.isDefined)
            it.webXml.get
          else
            Option(null)
        }))
    }
  }

  override def asMap = {
    super.asMap ++ Map[String, AnyRef](
      "file_extension" -> fileExtension,
      "web_xml" -> webXml
      )
  }

}