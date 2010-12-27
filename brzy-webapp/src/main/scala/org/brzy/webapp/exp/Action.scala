package org.brzy.webapp.exp

import org.slf4j.LoggerFactory

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
case class Action[F <: Function[_] :Manifest](path:String, action:F) {
  val log = LoggerFactory.getLogger(classOf[Action[_]])
  val manifestObject = manifest[F]
  log.debug(manifestObject)
}