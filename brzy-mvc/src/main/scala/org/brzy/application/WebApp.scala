package org.brzy.application

import collection.immutable.SortedSet
import org.brzy.config.AppConfig
import org.brzy.action.Action
import org.brzy.controller.{Path,Controller}
import org.slf4j.LoggerFactory
import collection.mutable.ListBuffer

/**
 * @author Michael Fortin
 * @version $Id: $
 */
abstract class WebApp(val config:AppConfig) {

  private val log = LoggerFactory.getLogger(classOf[WebApp])

  // TODO load plugins

  val controllers:Array[_<:java.lang.Object]
  val services:Array[_<:java.lang.Object]

  /**
   * The controllers are proxies so you have to get the super class
   */
  lazy val actions = {
    val list = new ListBuffer[Action]()
    controllers.foreach( ctl => {
      log.debug("load actions from controller: {}", ctl)
      val classPath = ctl.getClass.getSuperclass.getAnnotation(classOf[Controller])
      for(method <- ctl.getClass.getSuperclass.getMethods
          if method.getAnnotation(classOf[Path]) != null) {
        val methodPath = method.getAnnotation(classOf[Path])
        val pathValue = classPath.value +"/" +  methodPath.value
        val action = new Action(pathValue, method, ctl, config.views.file_extension)
        log.debug("action: " + action)
        list += action
      }
    })
    SortedSet[Action]() ++ list.toIterable
  }

  def startup = {
    log.info("application startup")
  }

  def shutdown = {
    log.info("application shutdown")
  }
}
