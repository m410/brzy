package org.brzy.security

import org.brzy.config.mod.ModProvider

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
class SecurityModProvider(config:SecurityModConfig) extends ModProvider {
  val name = config.name.get
  val defaultPath = config.defaultPath.get
}