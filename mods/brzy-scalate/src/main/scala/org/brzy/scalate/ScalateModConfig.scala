package org.brzy.scalate

import org.brzy.config.mod.Mod
import org.brzy.config.webapp.WebXml


/**
 * @author Michael Fortin
 * @version $Id : $
 */
class ScalateModConfig(map: Map[String, AnyRef]) extends Mod(map) with WebXml {
  override val configurationName = "Scalate"
  val fileExtension: Option[String] = map.get("file_extension").asInstanceOf[Option[String]].orElse(None)

  override val webXml: Option[List[Map[String, AnyRef]]] = map.get("web_xml").asInstanceOf[Option[List[Map[String, AnyRef]]]].orElse(None)

  override def <<(that: Mod):Mod  = {
    if (that == null) {
      this
    }
    else if(that.isInstanceOf[ScalateModConfig]) {
      val it = that.asInstanceOf[ScalateModConfig]
      new ScalateModConfig(Map[String, AnyRef](
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
          if (this.webXml.isDefined && it.webXml.isDefined &&
                  this.webXml.get != null && it.webXml.get != null)
            this.webXml.get ++ it.webXml.get
          else if (this.webXml.isDefined)
            this.webXml.get
          else if (it.webXml.isDefined)
            it.webXml.get
          else
            null
        }))
    }
    else {
      new ScalateModConfig(Map[String, AnyRef](
        "name" -> that.name.getOrElse(this.name.getOrElse(null)),
        "version" -> that.version.getOrElse(this.version.getOrElse(null)),
        "org" -> that.org.getOrElse(this.org.getOrElse(null)),
        "config_class" -> that.configClass.getOrElse(this.configClass.getOrElse(null)),
        "resource_class" -> that.resourceClass.getOrElse(this.resourceClass.getOrElse(null)),
        "file_extension" -> that.map.get("file_extension").getOrElse(this.fileExtension.getOrElse(null)),

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
        },
        "web_xml" -> {
          if (this.webXml.isDefined && this.webXml.get != null &&
                  that.map.get("web_xml").isDefined && that.map.get("web_xml").get != null)
            this.webXml.get ++ that.map.get("web_xml").get.asInstanceOf[List[_]]
          else if (this.webXml.isDefined)
            this.webXml.get.asInstanceOf[List[_]]
          else if (that.map.get("web_xml").isDefined)
            that.map.get("web_xml").get
          else
            null
        }))
    }
  }

  override def asMap:Map[String,AnyRef] = map

}