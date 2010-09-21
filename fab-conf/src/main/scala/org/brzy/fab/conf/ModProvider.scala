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
package org.brzy.fab.conf

/**
 * When an application is loaded an instance of the Module Provider is created.  The
 * Module Provider initialized and services at runtime and provides them to the
 * application.  Not all modules need a provider, some have no runtime services, like
 * the brzy tomcat module.
 *
 * @author Michael Fortin
 */
trait ModProvider {

  val name:String

  def startup:Unit = {}

  def shutdown:Unit = {}

  val serviceMap:Map[String,AnyRef] = Map()
}