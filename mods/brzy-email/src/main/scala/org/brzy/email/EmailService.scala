package org.brzy.email

import javax.mail.internet.{InternetAddress, MimeMessage}
import javax.mail.{MessagingException, Transport, Message=>Msg, Session}
import java.util.Properties
import java.beans.ConstructorProperties
import org.brzy.service.Service

/**
 * Sends a plain text email message. 
 *
 * @author Michael Fortin
 */
@Service
@ConstructorProperties(Array("emailModConfig"))
class EmailService(config:EmailModConfig) {
  val mailConfig = {
    val p = new java.util.Properties
    p.put("mail.host","")
    p.put("mail.from","")
    p.put("mail.user","")
    p.put("mail.store.protocol","")
    p.put("mail.transport.protocol","")
    p.put("mail.smtp.host","")
    p.put("mail.smtp.user","")
    p.put("mail.debug","")
    p
  }

  val fromAddress = config.mailFrom.get

  def send(msg: Message) = {
    val session: Session = Session.getDefaultInstance(mailConfig, null)
    val message: MimeMessage = new MimeMessage(session)
    message.setFrom(new InternetAddress(fromAddress))
    msg.to.map(a=> message.addRecipient(Msg.RecipientType.TO, new InternetAddress(a)))
    message.setSubject(msg.subject)
    message.setText(msg.body)
    Transport.send(message)
  }
}