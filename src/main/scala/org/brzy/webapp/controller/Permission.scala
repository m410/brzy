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
package org.brzy.webapp.controller

import org.brzy.webapp.action.args.Principal

import java.security.MessageDigest
import java.nio.charset.Charset


/**
 * Document Me..
 *
 * @author Michael Fortin
 */
trait Permission[T<:Authenticated] {
	def authenticator:Authenticator[T]
		
	protected[controller] val HEX_CHAR_TABLE = Array(
	    '0'.asInstanceOf[Byte], '1'.asInstanceOf[Byte], '2'.asInstanceOf[Byte], '3'.asInstanceOf[Byte],
	    '4'.asInstanceOf[Byte], '5'.asInstanceOf[Byte], '6'.asInstanceOf[Byte], '7'.asInstanceOf[Byte],
	    '8'.asInstanceOf[Byte], '9'.asInstanceOf[Byte], 'a'.asInstanceOf[Byte], 'b'.asInstanceOf[Byte],
	    'c'.asInstanceOf[Byte], 'd'.asInstanceOf[Byte], 'e'.asInstanceOf[Byte], 'f'.asInstanceOf[Byte])

	protected[controller] def encodeHex(data:Array[Byte]) = {
		val hex = new Array[Byte](2 * data.length)
    var index:Int = 0

    for (b <- data) {
      val v:Int = b & 0xFF;

      hex(index) = HEX_CHAR_TABLE(v >>> 4)
			index=index+1
      hex(index) = HEX_CHAR_TABLE(v & 0xF)
			index=index+1
    }
    new String(hex, "UTF8")
	}

	def login(userName:String, password:String):Option[T] = authenticator.login(userName,encrypt(password))

	def encrypt(password:String):String = {
		val messageDigest = MessageDigest.getInstance("MD5")
		encodeHex(messageDigest.digest(password.getBytes("UTF8")))
	}

	def principal(t:T) = {
		Principal(t.userName,Roles(t.authenticatedRoles:_*))
	}
}
