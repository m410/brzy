package org.brzy.webapp.exp

/**
 *
 */
trait Intercepted {
	def intercept(action:()=>AnyRef, actionArgs:List[AnyRef]):AnyRef = action()
}

