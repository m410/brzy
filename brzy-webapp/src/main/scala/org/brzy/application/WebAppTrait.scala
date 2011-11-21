package org.brzy.application

import org.brzy.webapp.controller.Controller
import org.brzy.interceptor.Invoker

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
trait WebAppTrait {

  def initializeServices:List[AnyRef]

  def initializeControllers:List[Controller]
}