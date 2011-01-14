package org.brzy.service

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
trait Authenticator[T<:Authenticated] {
	def login(user:String, pass:String):Option[T]
}