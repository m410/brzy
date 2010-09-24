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

import org.brzy.fab.conf.BaseConf
import org.brzy.fab.mod.PersistenceMod

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
class JpaModConfig(override val map: Map[String, AnyRef]) extends PersistenceMod(map) {
  val persistenceUnit: Option[String] = map.get("persistence_unit").asInstanceOf[Option[String]].orElse(None)

  override def <<(that: BaseConf) =
    if (that == null)
      this
    else
      new JpaModConfig(Map[String, AnyRef](
        "persistence_unit" -> that.map.getOrElse("persistence_unit", this.persistenceUnit.orNull)
        ) ++ super.<<(that).map)

}