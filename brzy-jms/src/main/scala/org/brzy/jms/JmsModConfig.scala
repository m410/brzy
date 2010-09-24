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
package org.brzy.jms

import org.brzy.fab.mod.RuntimeMod
import org.brzy.fab.conf.BaseConf

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
class JmsModConfig(override val map: Map[String, AnyRef]) extends RuntimeMod(map) {
  val connectionFactoryClass: Option[String] = map.get("connection_factory_class").asInstanceOf[Option[String]].orElse(None)
  val brokerUrl: Option[String] = map.get("broker_url").asInstanceOf[Option[String]].orElse(None)
  val userName: Option[String] = map.get("user_name").asInstanceOf[Option[String]].orElse(None)
  val password: Option[String] = map.get("password").asInstanceOf[Option[String]].orElse(None)
  val scanPackage: Option[String] = map.get("scan_package").asInstanceOf[Option[String]].orElse(None)

  override def <<(that: BaseConf) = {
    if (that == null) {
      this
    }
    else {
      new JmsModConfig(Map[String, AnyRef](
        "connection_factory_class" -> that.map.getOrElse("connection_factory_class", this.connectionFactoryClass.orNull),
        "broker_url" -> that.map.getOrElse("connection_url", this.brokerUrl.orNull)
        ) ++ super.<<(that).map)
    }
  }
}