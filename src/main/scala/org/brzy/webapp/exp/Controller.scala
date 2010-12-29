package org.brzy.webapp.exp

/**
 * 
 */
abstract class Controller(val basePath:String) extends Ordered[Controller] {
	val actions:List[Action]
  def compare(that: Controller) = basePath.compareTo(that.basePath)
  implicit def selfReference = this
}