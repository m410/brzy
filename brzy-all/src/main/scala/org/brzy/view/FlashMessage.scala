package org.brzy.util

import javax.servlet.http.HttpSession

/**
 * @author Michael Fortin
 * @version $Id:$
 */
class FlashMessage(message:String,session:HttpSession) {
	
	session.setAttribute("flash-message",this)
	
	def show = {
		session.removeAttribute("flash-message")
		message
	}
}