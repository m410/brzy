package org.brzy.webapp.exp


trait Intercepted {
	
	def intercept(action:()=>AnyRef, actionArgs:Array[AnyRef]):AnyRef = action()
}

