package org.brzy.webapp.controller

/**
 *
 */
trait Intercepted {
	def intercept(action:()=>AnyRef, actionArgs:List[AnyRef]):AnyRef = action()
}