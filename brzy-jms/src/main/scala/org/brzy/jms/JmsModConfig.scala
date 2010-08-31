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

import org.brzy.config.mod.Mod

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
class JmsModConfig(map: Map[String, AnyRef]) extends Mod(map) {
  override val configurationName = "JMS Configuration"
  val connectionFactoryClass:Option[String] = map.get("connection_factory_class").asInstanceOf[Option[String]].orElse(None)
  val brokerUrl:Option[String] = map.get("broker_url").asInstanceOf[Option[String]].orElse(None)
  val userName:Option[String] = map.get("user_name").asInstanceOf[Option[String]].orElse(None)
  val password:Option[String] = map.get("password").asInstanceOf[Option[String]].orElse(None)
  val scanPackage:Option[String] = map.get("scan_package").asInstanceOf[Option[String]].orElse(None)

  override def <<(that: Mod): Mod = {
    if (that == null) {
      this
    }
    else if (that.isInstanceOf[JmsModConfig]) {
      val it = that.asInstanceOf[JmsModConfig]
      new JmsModConfig(Map[String, AnyRef](
        "connection_factory_class" -> it.connectionFactoryClass.getOrElse(this.connectionFactoryClass.getOrElse(null)),
        "broker_url" -> it.brokerUrl.getOrElse(this.brokerUrl.getOrElse(null)))
              ++ super.<<(that).asMap)
    }
    else {
      new JmsModConfig(Map[String, AnyRef](
        "connection_factory_class" -> that.map.get("connection_factory_class").getOrElse(this.connectionFactoryClass.getOrElse(null)),
        "broker_url" -> that.map.get("connection_url").getOrElse(this.brokerUrl.getOrElse(null)))
              ++ super.<<(that).asMap)
    }
  }

  override def asMap = map
}