package org.brzy.spring

import org.brzy.config.mod.ModResource

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */

class SpringModResource(c:SpringModConfig) extends ModResource {
  val name = c.name.get
}