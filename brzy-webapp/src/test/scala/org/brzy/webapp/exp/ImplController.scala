package org.brzy.webapp.exp

import org.brzy.webapp.action.args.Parameters

class ImplController extends Controller("impls") with Secured with Intercepted {
	val actions = Array.empty[Action[_]] //Action("list",list _) :: Nil
	
	override def intercept(action:()=>AnyRef, actionArgs:Array[AnyRef]) = {
		 action()
	}
	
	def list(p:Parameters) = {
		
	}
}