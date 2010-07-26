package org.brzy.mvc.mock


class FooBar(var bar:Bar) {
  def hello(name:String) = bar.hello(name + ", from foobar")
}