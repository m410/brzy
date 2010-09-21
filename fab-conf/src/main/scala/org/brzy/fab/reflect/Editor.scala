/*
 * Copyright 2010 Michael Fortin <mike@brzy.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.brzy.fab.reflect

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