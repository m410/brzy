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
package org.brzy.webapp.persistence

import util.DynamicVariable

import Isolation._

/**
 * This is implemented by modules to add thread scope variable management to a
 * web application.
 * 
 * @author Michael Fortin
 */
trait SessionFactory {
  type T <: AnyRef

  def packageScope:String = ""

  def empty: T

  def context: DynamicVariable[T]

  def createSession(isolation:Isolation, readOnly:Boolean):T

  def destroySession(target:T)

}