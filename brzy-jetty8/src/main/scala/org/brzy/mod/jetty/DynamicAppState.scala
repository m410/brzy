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
package org.brzy.mod.jetty

//import org.brzy.application.WebApp

/**
 * The different states of the compiler.
 *
 * @author Michael Fortin
 */
sealed trait DynamicAppState

case class Running(webApp:AnyRef) extends DynamicAppState

object Compiling extends DynamicAppState

case class CompilerError(message:String) extends DynamicAppState

//object DisabledCompiler
