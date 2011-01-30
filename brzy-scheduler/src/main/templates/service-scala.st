package $packageName$

import org.brzy.mod.scheduler.Cron
import java.util.Date
import org.slf4j.LoggerFactory

/**
 * $className$ Scheduled Service
 */
class $className$ extends Cron {
  override val expression = "0/5 * * * * ?"
  val log = LoggerFactory.getLogger(classOf[$className$])

  def execute = {
    log.debug("run: " + new Date())
    // scheduled task
  }
}
