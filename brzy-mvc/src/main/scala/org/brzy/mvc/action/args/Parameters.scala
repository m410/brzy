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
package org.brzy.mvc.action.args

import collection.immutable._

/**
 * @author Michael Fortin
 */
class Parameters(map:collection.Map[String,Array[String]])
    extends Map[String,String] with MapLike[String, String, Parameters] {

  override def empty:Parameters = new Parameters(Map[String,Array[String]]())

  override def get(key: String): Option[String] =
    if(map.contains(key))
      Option(map(key)(0))
    else
      None

  def array(key:String):Option[Array[String]] = map.get(key)
  
  override def iterator: Iterator[(String, String)] = Iterator(map.map(f=> f._1 -> f._2(0)).toArray:_*)

  // does nothing
  override def + [B1 >: String](kv: (String, B1)): Parameters = this

  // does nothing
  override def -(key: String): Parameters =  this
}

