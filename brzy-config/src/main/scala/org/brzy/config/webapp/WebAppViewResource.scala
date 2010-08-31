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
package org.brzy.config.webapp

import org.brzy.config.mod.{Mod, ModProvider}

/**
 * Used by modules that provide view functionality to the application. While there can 
 * be many persitence modules in an application, here is only one view module.
 * 
 * @author Michael Fortin
 */
abstract class WebAppViewResource(module: Mod) extends ModProvider {
  val fileExtension:String
  val name = module.name.get
}