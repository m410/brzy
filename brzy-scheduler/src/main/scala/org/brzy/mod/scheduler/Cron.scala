package org.brzy.mod.scheduler

import org.brzy.service.Service

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
trait Cron extends Service {
  val expression = "0/5 * * * * ?"
  def execute:Unit
}