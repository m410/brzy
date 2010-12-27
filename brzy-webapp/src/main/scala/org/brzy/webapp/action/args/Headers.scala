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
package org.brzy.webapp.action.args

import collection.immutable.MapLike
import javax.servlet.http.HttpServletRequest
import collection.JavaConversions._


/**
 * A Action argument class to read header information.  Use it like
 * <pre>def myaction(c:Headers) = {
 *    val headerValue = c("name")
 * }
 * </pre>
 * 
 * @author Michael Fortin
 */
class Headers(request:HttpServletRequest) extends Map[String,String] with MapLike[String, String, Headers] {

  protected[args] val internalMap = {
    val map = collection.mutable.Map[String,String]()

    if(request != null)
      request.getHeaderNames.foreach(f=>{
        val str = f.asInstanceOf[String]
        map += str->request.getHeader(str).asInstanceOf[String]
      })
    
    map.toMap
  }

  override def empty:Headers = new Headers(null)

  def +[B1 >: String](kv: (String, B1)) = null

  def -(key: String) = null

  def iterator = internalMap.iterator

  def get(key: String) = internalMap.get(key)
}