package org.brzy.mod.scheduler

import org.brzy.service.Service

/**
 * Services that wish to be executed on a cron like schedule should implement this trait.
 * 
 * @author Michael Fortin
 */
trait Cron extends Service {

  /**
   * The cron expression used to calculate the next execution.
   */
  val expression = "0/5 * * * * ?"

  /**
   * the function called to execute the cron job.
   */
  def execute()
}