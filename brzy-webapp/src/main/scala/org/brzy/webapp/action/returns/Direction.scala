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
package org.brzy.webapp.action.returns

import xml.Elem

import com.twitter.json.{Json=>tJson}

/**
 * Where you want to send the result of the action.  Controller actions can only return one object
 * that is a Direction type.
 * 
 * @author Michael Fortin
 */
abstract class Direction 

/**
 * Override the default view.
 */
case class View(path:String) extends Direction

/**
 * Forward to another action without sending a redirect to the client.
 */
case class Forward(path:String) extends Direction {
  val contextPath = 
    if(path.startsWith("/"))
      path + ".brzy"
    else
      "/" + path + ".brzy" 

}

/**
 * Send a 302 redirect to the cleint.
 */
case class Redirect(path:String) extends Direction

/**
 * Return xml as the body of the response.
 */
case class Xml(t:AnyRef,contentType:String = "text/xml") extends Direction with Parser {
  import org.brzy.fab.reflect.Properties._

  def parse = {
    def node(name:String, elem:Elem) = elem.copy(label=name)
    val tmp = <class>
      {t.properties.map(p=> node({p._1}, <property>{p._2}</property>))}
    </class>
    node(t.getClass.getSimpleName,tmp)
  }.toString
}

/**
 * Returns plain text as the body of the response.
 */
case class Text(ref:AnyRef, contentType:String = "text/plain") extends Direction with Parser{
  def parse = ref.toString
}

/**
 * Returns binary data as the body of the response.  This is used to return files or images
 * as the response.
 */
case class Binary(bytes:Array[Byte], contentType:String) extends Direction

/**
 * Return Json formatted text as the body of the response.
 */
case class Json(target:AnyRef, contentType:String = "text/json") extends Direction with Parser {

  def parse = {
    import org.brzy.fab.reflect.Properties._
    tJson.build(target.properties).toString
  }

}

/**
 * Return an error to the client, eg. 403.
 */
case class Error(code:Int, msg:String) extends Direction