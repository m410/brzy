package org.brzy.email

import javax.mail.internet.{InternetAddress, MimeMessage}
import javax.mail.Authenticator
import javax.mail.PasswordAuthentication
import java.util.Properties
import java.beans.ConstructorProperties
import org.brzy.service.Service
import javax.mail._
import javax.mail.Message.RecipientType

/**
 * Sends a plain text email message. 
 *
 * @author Michael Fortin
 */
@Service
@ConstructorProperties(Array("emailModConfig"))
class EmailService(config:EmailModConfig) {

  private val mailConfig = {
    val p = new java.util.Properties
    p.put("mail.transport.protocol", config.transportProtocol.get)
    p.put("mail.smtp.auth", config.smtpAuth.get)
    p.put("mail.smtp.host",config.smtpHost.get)
    p.put("mail.debug",config.mailDebug.get)
    p
  }

  private val auth:Authenticator = 
      if(config.smtpAuth.get.equalsIgnoreCase("true")) {
        new Authenticator {
          override def getPasswordAuthentication:PasswordAuthentication = {
             new PasswordAuthentication(config.userName.get, config.password.get)
          }
        }
      }
      else {
        null
      }

  val fromAddress = config.mailFrom.get

  def send(msg: Message) = {
    val session: Session = Session.getDefaultInstance(mailConfig, auth)
    val message: MimeMessage = new MimeMessage(session)
    message.setFrom(new InternetAddress(fromAddress))
    msg.to.map(a=> message.addRecipient(RecipientType.TO, new InternetAddress(a)))
    message.setSubject(msg.subject)
    message.setText(msg.body)
    Transport.send(message)
  }
}