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
package org.brzy.mod.calista

import org.brzy.fab.conf.BaseConf
import org.brzy.fab.mod.PersistenceMod

/**
 *
 * @author Michael Fortin
 */
class CalistaModConf(override val map: Map[String, AnyRef]) extends PersistenceMod(map) {

  val userName: Option[String] = map.get("user_name").asInstanceOf[Option[String]].orElse(None)
  val password: Option[String] = map.get("password").asInstanceOf[Option[String]].orElse(None)

  val keySpace: Option[String] = map.get("key_space").asInstanceOf[Option[String]].orElse(None)

  val host: Option[String] = map.get("host").asInstanceOf[Option[String]].orElse(None)
  val port: Option[Int] = map.get("port").asInstanceOf[Option[Int]].orElse(Option(9160))

  val createSchema: Option[Boolean] = map.get("create_schema").asInstanceOf[Option[Boolean]].orElse(Option(false))

  override def <<(that: BaseConf) = {
    if (that == null) {
      this
    }
    else  {
      new CalistaModConf(Map[String, AnyRef](
        "host" -> that.map.getOrElse("host",this.host.orNull),
        "port" -> that.map.getOrElse("port",Integer.valueOf(9160)),
        "create_schema" -> that.map.getOrElse("create_schema",java.lang.Boolean.FALSE),
        "user_name" -> that.map.getOrElse("user_name",this.userName.orNull),
        "password" -> that.map.getOrElse("password",this.password.orNull),
        "key_space" -> that.map.getOrElse("key_space",this.keySpace.orNull))
        ++ super.<<(that).map)
    }
  }
}