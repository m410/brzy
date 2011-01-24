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
package org.brzy.webapp.action

import HttpMethod._

/**
 * Set constraints upon actions to refine the action selection process.
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
trait Constraint

case class Roles(allowed:String*) extends Constraint

case class HttpMethods(allowed:HttpMethod*) extends Constraint

case class Secure(secure:Boolean = true) extends Constraint

case class ContentTypes(allowed:String*) extends Constraint

