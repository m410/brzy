package org.brzy.webapp.controller

import org.brzy.webapp.action.Action

/**
 * 
 */
abstract class Controller(val basePath:String) extends Ordered[Controller] {
	implicit def selfReference = this
  def actions:List[Action]
  def compare(that: Controller) = basePath.compareTo(that.basePath)
}