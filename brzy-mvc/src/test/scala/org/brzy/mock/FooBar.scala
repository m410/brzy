package org.brzy.saved.intercept


class FooBar(var bar:Bar) {
  def hello(name:String) = bar.hello(name + ", from foobar")
}