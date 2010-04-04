package org.brzy.saved.intercept

/**
 * @author Michael Fortin
 * @version $Id: $
 */
trait Foo {
  def hello:Unit
}
class FooImpl extends Foo{
  def hello = println("hello")
}

