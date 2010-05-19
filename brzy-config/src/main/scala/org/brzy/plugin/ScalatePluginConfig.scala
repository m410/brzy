package org.brzy.plugin

/**
 * @author Michael Fortin
 * @version $Id : $
 */
class ScalatePluginConfig(map: Map[String, AnyRef]) extends Plugin[ScalatePluginConfig](map) {
  val fileExtension = set[String](map.get("file_extension"))

  val webXml = makeWebXml(map.get("web_xml"))

  val configurationName = "Scalate"

  override def +(that: ScalatePluginConfig) = new ScalatePluginConfig(this.asMap ++ that.asMap)

  override def asMap = {
    val map = new collection.mutable.HashMap[String, AnyRef]()
    map.put("file_extension", fileExtension)
    map.put("web_xml", webXml)
    super.asMap ++ map
  }

}