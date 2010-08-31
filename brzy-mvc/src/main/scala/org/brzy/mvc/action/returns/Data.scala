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

/**
 * @author Michael Fortin
 */
abstract class Data

  case class Model(attrs:Tuple2[String,AnyRef]*) extends Data

  case class CookieAdd(attrs:Tuple2[String,AnyRef]) extends Data

  case class SessionAdd(attrs:Tuple2[String,AnyRef]*) extends Data

  case class SessionRemove(attr:String) extends Data

  case class Flash(code:String,default:String) extends Data
