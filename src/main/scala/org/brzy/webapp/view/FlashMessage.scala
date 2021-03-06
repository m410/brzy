/*
 * Copyright 2010 Michael Fortin <mike@brzy.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.brzy.webapp.view

import javax.servlet.http.HttpSession

/**
 * This is the session scope attribute that that holds a flash message and a reference to it's
 * session.  Once it's called by the client, it removes itself from the http session.
 * 
 * @author Michael Fortin
 */
class FlashMessage(message:String,session:HttpSession) {
	
	session.setAttribute("flash-message",this)

	// TODO needs to get the message from the i18n configuration
	def show = {
		session.removeAttribute("flash-message")
		message
	}

  override def toString = "FlashMessage("+message+", "+session+")"
}