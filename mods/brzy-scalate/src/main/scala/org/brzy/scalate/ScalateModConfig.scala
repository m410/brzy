package org.brzy.scalate

import org.brzy.config.mod.Mod
import org.brzy.config.webapp.WebXml


/**
 * @author Michael Fortin
 */
class ScalateModConfig(map: Map[String, AnyRef]) extends Mod(map) with WebXml {
  override val configurationName = "Scalate"
  val fileExtension: Option[String] = map.get("file_extension").asInstanceOf[Option[String]].orElse(None)

  override val webXml: Option[List[Map[String, AnyRef]]] = map.get("web_xml").asInstanceOf[Option[List[Map[String, AnyRef]]]].orElse(None)

  override def <<(that: Mod): Mod = {
    if (that == null) {
      this
    }
    else if (that.isInstanceOf[ScalateModConfig]) {
      val it = that.asInstanceOf[ScalateModConfig]
      new ScalateModConfig(Map[String, AnyRef](
        "file_extension" -> it.fileExtension.getOrElse(this.fileExtension.getOrElse(null)),
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
        }) ++ super.<<(that).asMap)
    }
    else {
      new ScalateModConfig(Map[String, AnyRef](
        "file_extension" -> that.map.get("file_extension").getOrElse(this.fileExtension.getOrElse(null)),
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
        }) ++ super.<<(that).asMap)
    }
  }

  override def asMap: Map[String, AnyRef] = map

}