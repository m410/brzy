package $packageName$

import org.brzy.mod.jms.Queue

/**
 *
 */
@Queue(destination="test.queue")
class $className$ {
  val log = LoggerFactory.getLogger(classOf[$className$])

  def onMessage(msg:String) ={
    log.debug("msg: "  + msg)
    // listener
  }
}
