package org.brzy.scheduler

import org.brzy.config.mod.Mod

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
class SchedulerModConfig(map: Map[String, AnyRef]) extends Mod(map) {
  override val configurationName = "Scheduler Configuration"
}