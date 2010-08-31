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
package org.brzy.cascal

import org.brzy.config.mod.Mod

/**
 *
 * @author Michael Fortin
 */
class CascalModConfig(map: Map[String, AnyRef]) extends Mod(map) {
  override val configurationName = "Cascal Configuration"
  val userName: Option[String] = map.get("user_name").asInstanceOf[Option[String]].orElse(None)
  val password: Option[String] = map.get("password").asInstanceOf[Option[String]].orElse(None)
  val keySpace: Option[String] = map.get("key_space").asInstanceOf[Option[String]].orElse(None)
  val keyFamily: Option[String] = map.get("key_family").asInstanceOf[Option[String]].orElse(None)
  
  override def <<(that: Mod):Mod = {
    if (that == null) {
      this
    }
    else if(that.isInstanceOf[CascalModConfig]) {
      val it = that.asInstanceOf[CascalModConfig]
      new CascalModConfig(Map[String, AnyRef](
        "user_name" -> it.userName.getOrElse(this.userName.getOrElse(null)),
        "password" -> it.password.getOrElse(this.password.getOrElse(null)),
        "key_space" -> it.keySpace.getOrElse(this.keySpace.getOrElse(null)),
        "key_family" -> it.keyFamily.getOrElse(this.keyFamily.getOrElse(null)))
        ++ super.<<(that).asMap)
    }
    else {
      new CascalModConfig(Map[String, AnyRef](
        "user_name" -> that.map.get("user_name").getOrElse(this.userName.getOrElse(null)),
        "password" -> that.map.get("password").getOrElse(this.password.getOrElse(null)),
        "key_space" -> that.map.get("key_space").getOrElse(this.keySpace.getOrElse(null)),
        "key_family" -> that.map.get("key_family").getOrElse(this.keyFamily.getOrElse(null)))
        ++ super.<<(that).asMap)
    }
  }

  override def asMap = map

}