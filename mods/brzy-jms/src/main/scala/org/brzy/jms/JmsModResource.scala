package org.brzy.jms

import org.brzy.config.mod.ModResource

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
class JmsModResource(c:JmsModConfig) extends ModResource {
  val name = c.name.get

}