package org.brzy.plugin


import java.util.{Map => JMap}
import collection.JavaConversions._

/**
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
class CascalPluginConfig(map:Map[String,AnyRef]) extends Plugin[CascalPluginConfig](map) {


  val configurationName = "Cascal"

  def +(that: CascalPluginConfig) = new CascalPluginConfig(this.asMap ++ that.asMap)

  def asMap = {
    val map = Map[String,AnyRef]()
    // TODO add each property
    map
  }
}