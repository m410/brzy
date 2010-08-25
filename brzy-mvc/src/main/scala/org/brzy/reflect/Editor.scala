package org.brzy.reflect

/**
 * This works much like the java beans PropertyEditor.  It's purpose is to help in the
 * conversion of properties in constructs of scala classes.
 * 
 * @author Michael Fortin
 */
trait Editor {
  def text: String
  def text_=(str:String)

  def value:Any
  def value_=(v:Any)

  def toText(v:Any):String
  def toValue(v:String):Any
}