package org.brzy.mock
import org.brzy.config.mod.Mod

class MockModConfig(m:Map[String,AnyRef]) extends Mod(m) {
  override val configurationName = "Mock Plugin"


  override def <<(that: Mod) = {
    if(that == null) {
      this
    }
    else {
      new MockModConfig(m)
    }
  }
}