package org.brzy.mvc.mock


trait Foo {
  def hello:Unit
}
class FooImpl extends Foo{
  def hello = println("hello")
}

