package org.brzy.security.mock


case class Person(key:String,
		firstName:String)

object Person {
  def get(key:String) = Person(key,"Fred")
}