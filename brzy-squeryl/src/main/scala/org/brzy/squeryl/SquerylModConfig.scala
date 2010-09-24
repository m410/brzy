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
package org.brzy.squeryl

import org.brzy.fab.conf.BaseConf
import org.brzy.fab.mod.PersistenceMod


/**
 * @author Michael Fortin
 */
class SquerylModConfig(override val map: Map[String, AnyRef]) extends PersistenceMod(map) {
  val driver: Option[String] = map.get("driver").asInstanceOf[Option[String]].orElse(None)
  val url: Option[String] = map.get("url").asInstanceOf[Option[String]].orElse(None)
  val userName: Option[String] = map.get("user_name").asInstanceOf[Option[String]].orElse(None)
  val password: Option[String] = map.get("password").asInstanceOf[Option[String]].orElse(None)
  val adaptorName: Option[String] = map.get("adaptor_name").asInstanceOf[Option[String]].orElse(None)

  override def <<(that: BaseConf) =
    if (that == null)
      this
    else
      new SquerylModConfig(Map[String, AnyRef](
        "driver" -> that.map.getOrElse("driver", this.driver.orNull),
        "url" -> that.map.getOrElse("url", this.url.orNull),
        "user_name" -> that.map.getOrElse("user_name", this.userName.orNull),
        "password" -> that.map.getOrElse("password", this.password.orNull),
        "adaptor_name" -> that.map.getOrElse("adapter_name", this.adaptorName.orNull)
        ) ++ super.<<(that).map)
}