package org.brzy.mvc.view

import javax.servlet.http.HttpSession

/**
 * @author Michael Fortin
 */
class FlashMessage(message:String,session:HttpSession) {
	
	session.setAttribute("flash-message",this)
	
	def show = {
		session.removeAttribute("flash-message")
		message
	}
}