package org.brzy.mock
import org.brzy.config.plugin.Plugin
/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
class MockPluginConfig(m:Map[String,AnyRef]) extends Plugin(m) {
  override val configurationName = "Mock Plugin"


  override def <<(that: Plugin) = {
    if(that == null) {
      this
    }
    else {
      new MockPluginConfig(m)
    }
  }
}