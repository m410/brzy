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

/**
 * Abstract class for the different model centric return types.  An action can return any number
 * of these.
 * <pre>def myAction = (Model("a->"b"),CookieAdd("a->"b"))
 * </pre>
 * @author Michael Fortin
 */
abstract class Data

/**
 *  Add a name value pair to the servlet request attributes.
 */
case class Model(attrs:Tuple2[String,AnyRef]*) extends Data

/**
 * Add a cookie to the return headers.
 */
case class CookieAdd(attrs:Tuple2[String,AnyRef]) extends Data

/**
 * add an attribute to the httpSession.
 */
case class SessionAdd(attrs:Tuple2[String,AnyRef]*) extends Data

/**
 * Remove an attribute for the http session.
 */
case class SessionRemove(attr:String) extends Data

/**
 * Add an attribute to the http session that is only available for a single
 * request by the client.
 */
case class Flash(code:String,default:String) extends Data
