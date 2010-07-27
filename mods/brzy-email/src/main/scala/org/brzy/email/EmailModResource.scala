package org.brzy.email

import org.brzy.config.mod.ModResource

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */

class EmailModResource(c:EmailModConfig) extends ModResource {
  val name = c.name.get

}