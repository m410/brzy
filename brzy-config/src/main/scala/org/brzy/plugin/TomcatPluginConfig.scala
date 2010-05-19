package org.brzy.plugin


/**
 * @author Michael Fortin
 * @version $Id: $
 */
class TomcatPluginConfig(map:Map[String,AnyRef]) extends Plugin[TomcatPluginConfig](map) {

  val configurationName = "Tomcat"

  override def +(that: TomcatPluginConfig) = new TomcatPluginConfig(this.asMap ++ that.asMap)

  override def asMap = super.asMap

}