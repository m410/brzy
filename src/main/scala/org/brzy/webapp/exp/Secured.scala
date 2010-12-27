package org.brzy.webapp.exp


trait Secured {
	val defaultRoles:Array[String] = Array("ADMIN")
	val actionRoles:Map[String,Array[String]] = Map.empty[String,Array[String]]
}