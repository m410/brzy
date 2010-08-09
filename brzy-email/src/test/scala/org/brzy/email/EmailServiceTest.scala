package org.brzy.email

import org.scalatest.junit.JUnitSuite
import org.junit.{Ignore, Test}


class EmailServiceTest extends JUnitSuite {

  // this works, but needs a valid user/pass
  @Test @Ignore def testSendMessage = {
    val config = new EmailModConfig(Map(
        "smtp_host" -> "smtp.comcast.net",
        "smtp_auth" -> "true",
        "user_name" -> "****",
        "password" -> "****",
        "mail_transport_protocol" -> "smtp",
        "mail_debug" -> "true",
        "mail_from" -> "mfortin@m410.com"
      ))
    val service = new EmailService(config)
    service.send(Message(Array("michael@m410.us"),"Test Message","This is just a test"))
  }
}