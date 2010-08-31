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
package org.brzy.spring

import org.brzy.config.mod.Mod

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
class SpringModConfig(map: Map[String, AnyRef]) extends Mod(map) {
  override val configurationName = "Email Configuration"
  val applicationContext: Option[String] = map.get("application_context").asInstanceOf[Option[String]].orElse(None)

  override def <<(that: Mod): Mod = {
    if (that == null) {
      this
    }
    else if (that.isInstanceOf[SpringModConfig]) {
      val it = that.asInstanceOf[SpringModConfig]
      new SpringModConfig(Map[String, AnyRef](
        "application_context" -> it.applicationContext.getOrElse(this.applicationContext.getOrElse(null)))
              ++ super.<<(that).asMap)
    }
    else {
      new SpringModConfig(Map[String, AnyRef](
        "application_context" -> that.map.get("application_context").getOrElse(this.applicationContext.getOrElse(null)))
              ++ super.<<(that).asMap)
    }
  }

  override def asMap = map
}