package org.brzy.scheduler

import org.brzy.config.mod.ModResource

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */

class SchedulerModResource(c:SchedulerModConfig) extends ModResource {
  val name = c.name.get

}