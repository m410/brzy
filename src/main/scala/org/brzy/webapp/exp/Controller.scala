package org.brzy.webapp.exp


abstract class Controller(val basePath:String) {
	val actions:Array[Action]
}