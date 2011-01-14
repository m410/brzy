package org.brzy.webapp.controller

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
trait Authenticated {
	def userName:String
	def password:String
	def authenticatedRoles:Array[String]
}