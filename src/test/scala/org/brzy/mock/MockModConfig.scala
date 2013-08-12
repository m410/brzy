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
package org.brzy.mock

import org.brzy.fab.conf.BaseConf
import org.brzy.fab.mod.ViewMod

class MockModConfig(override val map:Map[String,AnyRef]) extends ViewMod(map) {

  override def <<(it: BaseConf) = {
    if(it == null)
      this
    else {
      val that = it.asInstanceOf[ViewMod]
      new MockModConfig(super.<<(that).map)
    }
  }

}