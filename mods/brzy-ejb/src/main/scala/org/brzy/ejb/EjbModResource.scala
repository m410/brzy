package org.brzy.ejb

import org.brzy.config.mod.ModResource

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */

class EjbModResource(c:EjbModConfig) extends ModResource {
  val name = c.name.get

}