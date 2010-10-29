package $packageName$

import org.brzy.mod.scheduler.Cron
import java.util.Date

/**
 * $className$ Scheduled Service
 */
@Cron("0/5 * * * * ?")
class $className$ {
  val log = LoggerFactory.getLogger(classOf[$className$])

  def execute = {
    log.debug("run: " + new Date())
    // scheduled task
  }
}
