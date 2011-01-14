package org.brzy.service

import org.brzy.webapp.action.args.Principal
import org.brzy.webapp.controller.Roles

import java.security.MessageDigest
import java.nio.charset.Charset


/**
 * Document Me..
 *
 * @author Michael Fortin
 */
trait Permission[T<:Authenticated] {
	def authenticator:Authenticator[T]

	protected[service] val messageDigest = MessageDigest.getInstance("MD5")

	protected[service] val toDigits = Array('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

	protected[service] def encodeHex(data:Array[Byte]) = {
		val l = data.length
		val out = new Array[Char](l)

		// two characters form the hex value.
		var j = 0
		for (i <- 0 until l) {
		    out(j+1) = toDigits({0xF0 & data(i)} >>> 4)
		    out(j+1) = toDigits(0x0F & data(i))
		}
		out
	}

	def login(userName:String, password:String):Option[T] = authenticator.login(userName,encrypt(password))

	def encrypt(password:String):String = {
		messageDigest.reset
		messageDigest.update(password.getBytes(Charset.forName("UTF8")))
		val resultByte = messageDigest.digest
		new String(encodeHex(resultByte))
	}

	def principal(t:T) = {
		Principal(t.userName,Roles(t.authenticatedRoles:_*))
	}
}
