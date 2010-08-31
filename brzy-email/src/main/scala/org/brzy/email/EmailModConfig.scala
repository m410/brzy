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
package org.brzy.email

import org.brzy.config.mod.Mod

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
class EmailModConfig(map: Map[String, AnyRef]) extends Mod(map) {
  override val configurationName = "Email Configuration"
  val smtpHost: Option[String] = map.get("smtp_host").asInstanceOf[Option[String]].orElse(None)
  val smtpAuth: Option[String] = map.get("smtp_auth").asInstanceOf[Option[String]].orElse(None)

  val userName: Option[String] = map.get("user_name").asInstanceOf[Option[String]].orElse(None)
  val password: Option[String] = map.get("password").asInstanceOf[Option[String]].orElse(None)

  val mailFrom: Option[String] = map.get("mail_from").asInstanceOf[Option[String]].orElse(None)
//  val mailUser: Option[String] = map.get("mail_user").asInstanceOf[Option[String]].orElse(None)
//  val storeProtocol: Option[String] = map.get("mail_store_protocol").asInstanceOf[Option[String]].orElse(None)

  val transportProtocol: Option[String] = map.get("mail_transport_protocol").asInstanceOf[Option[String]].orElse(None)
  val mailDebug: Option[String] = map.get("mail_debug").asInstanceOf[Option[String]].orElse(None)

  override def <<(that: Mod): Mod = {
    if (that == null) {
      this
    }
    else if (that.isInstanceOf[EmailModConfig]) {
      val it = that.asInstanceOf[EmailModConfig]
      new EmailModConfig(Map[String, AnyRef](
        "smtp_host" -> it.smtpHost.getOrElse(this.smtpHost.getOrElse(null)),
        "smtp_auth" -> it.smtpAuth.getOrElse(this.smtpAuth.getOrElse(null)),
        "user_name" -> it.userName.getOrElse(this.userName.getOrElse(null)),
        "password" -> it.password.getOrElse(this.password.getOrElse(null)),
        "mail_from" -> it.mailFrom.getOrElse(this.mailFrom.getOrElse(null)),
        "mail_transport_protocol" -> it.transportProtocol.getOrElse(this.transportProtocol.getOrElse(null)),
        "mail_debug" -> it.mailDebug.getOrElse(this.mailDebug.getOrElse(null))
        ) ++ super.<<(that).asMap)
    }
    else {
      new EmailModConfig(Map[String, AnyRef](
        "smtp_host" -> that.map.get("smtp_host").getOrElse(this.smtpHost.getOrElse(null)),
        "smtp_auth" -> that.map.get("smtp_auth").getOrElse(this.smtpAuth.getOrElse(null)),
        "user_name" -> that.map.get("user_name").getOrElse(this.userName.getOrElse(null)),
        "password" -> that.map.get("password").getOrElse(this.password.getOrElse(null)),
        "mail_from" -> that.map.get("mail_from").getOrElse(this.mailFrom.getOrElse(null)),
        "mail_transport_protocol" -> that.map.get("mail_transport_protocol").getOrElse(this.transportProtocol.getOrElse(null)),
        "mail_debug" -> that.map.get("mail_debug").getOrElse(this.mailDebug.getOrElse(null))
        ) ++ super.<<(that).asMap)
    }
  }

  override def asMap = map
}