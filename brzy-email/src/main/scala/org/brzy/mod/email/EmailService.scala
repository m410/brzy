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
package org.brzy.mod.email

import javax.mail.internet.{InternetAddress, MimeMessage}
import javax.mail.Authenticator
import javax.mail.PasswordAuthentication
import java.beans.ConstructorProperties
import javax.mail._
import javax.mail.Message.RecipientType

import org.brzy.service.Service

/**
 * Sends a plain text email message. 
 *
 * @author Michael Fortin
 */
@ConstructorProperties(Array("emailModConfig"))
class EmailService(config:EmailModConfig) extends Service{

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