package org.brzy.tomcat

import org.brzy.config.plugin.Plugin


/**
 * @author Michael Fortin
 * @version $Id: $
 */
class TomcatPluginConfig(map:Map[String,AnyRef]) extends Plugin(map) {
  override val configurationName = "Tomcat"
  override def asMap:Map[String,AnyRef] = map
  override def <<(that: Plugin):Plugin  = super.<<(that)
}