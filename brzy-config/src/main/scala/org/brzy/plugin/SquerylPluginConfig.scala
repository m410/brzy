package org.brzy.plugin

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class SquerylPluginConfig(map:Map[String,AnyRef]) extends Plugin[SquerylPluginConfig](map) {

  val driver = set[String](map.get("driver"))
  val url = set[String](map.get("url"))
  val userName = set[String](map.get("user_name"))
  val password = set[String](map.get("password"))
  val adaptorName = set[String](map.get("adaptor_name"))

  val configurationName = "Squeryl"

  def +(that: SquerylPluginConfig) = new SquerylPluginConfig(this.asMap ++ that.asMap)

  def asMap = {
    val map = Map[String,AnyRef]()
    // TODO add each property
    map
  }
}