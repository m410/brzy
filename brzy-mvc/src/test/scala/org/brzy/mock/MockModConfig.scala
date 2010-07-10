package org.brzy.mock

import org.brzy.config.mod.Mod

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */

class MockModConfig(m:Map[String,AnyRef]) extends Mod(m) {
  override val configurationName = "Mock Module"


  override def <<(that: Mod) = {
    if(that == null) {
      this
    }
    else {
      new MockModConfig(m)
    }
  }
}