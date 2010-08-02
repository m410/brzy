package org.brzy.security

import org.brzy.config.mod.ModResource

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
class SecurityModResource(config:SecurityModConfig) extends ModResource {
  val name = config.name.get
  val defaultPath = config.defaultPath.get
}