package org.brzy.plugin

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class ScalatePluginConfig(map:Map[String,AnyRef]) extends Plugin[ScalatePluginConfig](map) {

  val file_extension = set[String](map.get("file_extension"))

  val webXml = makeWebXml(map.get("web_xml"))

  val configurationName = "Scalate"

  def +(that: ScalatePluginConfig) = new ScalatePluginConfig(this.asMap ++ that.asMap)

  def asMap = {
    val map = Map[String,AnyRef]()
    // TODO add each property
    map
  }
}