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
package org.brzy.mvc.action.returns

//import com.twitter.json.{Json=>tJson}

/**
 * Where you want to send the result of the action too.
 * 
 * @author Michael Fortin
 */
abstract class Direction 

  case class View(path:String) extends Direction

  case class Forward(path:String) extends Direction

  case class Redirect(path:String) extends Direction

  case class Xml(t:AnyRef) extends Direction with Parser {
    def parse = {<class>{t.getClass.getSimpleName}</class>}.toString
    val contentType = "text/xml"
  }

  case class Text(ref:AnyRef) extends Direction with Parser{
    def parse = ref.toString
    val contentType = "text"
  }

  case class Binary(bytes:Array[Byte], contentType:String) extends Direction

  case class Json(t:AnyRef) extends Direction with Parser {
    def parse = "{}"//tJson.build(Map("class"->t.getClass.getSimpleName))
    val contentType = "text/json"
  }

  case class Error(code:Int, msg:String) extends Direction