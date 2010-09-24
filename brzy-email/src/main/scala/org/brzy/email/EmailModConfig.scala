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

import org.brzy.fab.conf.BaseConf
import org.brzy.fab.mod.RuntimeMod

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
class EmailModConfig(override val map: Map[String, AnyRef]) extends RuntimeMod(map) {
  val smtpHost: Option[String] = map.get("smtp_host").asInstanceOf[Option[String]].orElse(None)
  val smtpAuth: Option[String] = map.get("smtp_auth").asInstanceOf[Option[String]].orElse(None)

  val userName: Option[String] = map.get("user_name").asInstanceOf[Option[String]].orElse(None)
  val password: Option[String] = map.get("password").asInstanceOf[Option[String]].orElse(None)

  val mailFrom: Option[String] = map.get("mail_from").asInstanceOf[Option[String]].orElse(None)
  //  val mailUser: Option[String] = map.get("mail_user").asInstanceOf[Option[String]].orElse(None)
  //  val storeProtocol: Option[String] = map.get("mail_store_protocol").asInstanceOf[Option[String]].orElse(None)

  val transportProtocol: Option[String] = map.get("mail_transport_protocol").asInstanceOf[Option[String]].orElse(None)
  val mailDebug: Option[String] = map.get("mail_debug").asInstanceOf[Option[String]].orElse(None)

  override def <<(that: BaseConf) = {
    if (that == null) {
      this
    }
    else {
      new EmailModConfig(Map[String, AnyRef](
        "smtp_host" -> that.map.getOrElse("smtp_host", this.smtpHost.orNull),
        "smtp_auth" -> that.map.getOrElse("smtp_auth", this.smtpAuth.orNull),
        "user_name" -> that.map.getOrElse("user_name", this.userName.orNull),
        "password" -> that.map.getOrElse("password", this.password.orNull),
        "mail_from" -> that.map.getOrElse("mail_from", this.mailFrom.orNull),
        "mail_transport_protocol" -> that.map.getOrElse("mail_transport_protocol", this.transportProtocol.orNull),
        "mail_debug" -> that.map.getOrElse("mail_debug", this.mailDebug.orNull)
        ) ++ super.<<(that).map)
    }
  }
}