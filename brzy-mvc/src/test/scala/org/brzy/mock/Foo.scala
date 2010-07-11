package org.brzy.saved.intercept


trait Foo {
  def hello:Unit
}
class FooImpl extends Foo{
  def hello = println("hello")
}

