package org.brzy.plugin


import java.util.{Map => JMap}
import collection.JavaConversions._

/**
 *
 * @author Michael Fortin
 * @version $Id : $
 */
class CascalPluginConfig(map: Map[String, AnyRef]) extends Plugin[CascalPluginConfig](map) {
  val configurationName = "Cascal"

  override def +(that: CascalPluginConfig) = new CascalPluginConfig(this.asMap ++ that.asMap)

  override def asMap = super.asMap

}