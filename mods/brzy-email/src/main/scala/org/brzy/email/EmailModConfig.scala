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
  val userName: Option[String] = map.get("user_name").asInstanceOf[Option[String]].orElse(None)
  val password: Option[String] = map.get("password").asInstanceOf[Option[String]].orElse(None)

  val mailFrom: Option[String] = map.get("mail_from").asInstanceOf[Option[String]].orElse(None)

  val mailUser: Option[String] = map.get("mail_user").asInstanceOf[Option[String]].orElse(None)
  val mailStoreProtocol: Option[String] = map.get("mail_store_protocol").asInstanceOf[Option[String]].orElse(None)
  val mailTransportProtocol: Option[String] = map.get("mail_transport_protocol").asInstanceOf[Option[String]].orElse(None)

  val mailSmtpUser: Option[String] = map.get("mail_smpt_user").asInstanceOf[Option[String]].orElse(None)
  val mailDebug: Option[String] = map.get("mail_debug").asInstanceOf[Option[String]].orElse(None)

  override def <<(that: Mod): Mod = {
    if (that == null) {
      this
    }
    else if (that.isInstanceOf[EmailModConfig]) {
      val it = that.asInstanceOf[EmailModConfig]
      new EmailModConfig(Map[String, AnyRef](
        "user_name" -> it.userName.getOrElse(this.userName.getOrElse(null)),
        "password" -> it.password.getOrElse(this.password.getOrElse(null)),
        "smtp_host" -> it.smtpHost.getOrElse(this.smtpHost.getOrElse(null)))
              ++ super.<<(that).asMap)
    }
    else {
      new EmailModConfig(Map[String, AnyRef](
        "user_name" -> that.map.get("user_name").getOrElse(this.userName.getOrElse(null)),
        "password" -> that.map.get("password").getOrElse(this.password.getOrElse(null)),
        "smtp_host" -> that.map.get("smtp_host").getOrElse(this.smtpHost.getOrElse(null)))
              ++ super.<<(that).asMap)
    }
  }

  override def asMap = map
}