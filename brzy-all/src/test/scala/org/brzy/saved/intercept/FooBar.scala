package org.brzy.saved.intercept

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */

class FooBar(var bar:Bar) {
  def hello(name:String) = bar.hello(name + ", from foobar")
}