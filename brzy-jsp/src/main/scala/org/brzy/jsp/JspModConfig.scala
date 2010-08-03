package org.brzy.jsp

import org.brzy.config.mod.Mod

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
class JspModConfig(map: Map[String, AnyRef]) extends Mod(map) {
  override val configurationName = "Email Configuration"

  override def asMap = map
}