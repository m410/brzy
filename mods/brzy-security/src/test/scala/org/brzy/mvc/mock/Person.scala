package org.brzy.mock


case class Person(key:String,
		firstName:String)

object Person {
  def get(key:String) = Person(key,"Fred")
}