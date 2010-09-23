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
package org.brzy.jpa

import org.brzy.fab.mod.Mod
import org.brzy.fab.conf.BaseConf

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
class JpaModConfig(override val map: Map[String, AnyRef]) extends Mod(map) {
  val persistenceUnit: Option[String] = map.get("persistence_unit").asInstanceOf[Option[String]].orElse(None)

  override def <<(that: BaseConf) = {
    if (that == null) {
      this
    }
    else if (that.isInstanceOf[JpaModConfig]) {
      val it = that.asInstanceOf[JpaModConfig]
      new JpaModConfig(Map[String, AnyRef](
        "persistence_unit" -> it.persistenceUnit.getOrElse(this.persistenceUnit.getOrElse(null)))
              ++ super.<<(that).map)
    }
    else {
      new JpaModConfig(Map[String, AnyRef](
        "persistence_unit" -> that.map.get("persistence_unit").getOrElse(this.persistenceUnit.getOrElse(null)))
              ++ super.<<(that).map)
    }
  }
}