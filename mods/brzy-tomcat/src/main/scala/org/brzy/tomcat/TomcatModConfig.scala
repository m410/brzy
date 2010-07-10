package org.brzy.tomcat

import org.brzy.config.mod.Mod

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class TomcatModConfig(map:Map[String,AnyRef]) extends Mod(map) {
  override val configurationName = "Tomcat"
  override def asMap:Map[String,AnyRef] = map
  override def <<(that: Mod):Mod  = super.<<(that)
}