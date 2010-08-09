package org.brzy.email

import org.scalatest.junit.JUnitSuite
import org.junit.Test

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */

class EmailServiceTest extends JUnitSuite {
  @Test def testSendMessage = {
    val config = new EmailModConfig(Map(
        "smtp_host" -> "smtp.comcast.net",
        "smtp_auth" -> "true",
        "user_name" -> "mf410",
        "password" -> "xalan23y",
        "mail_transport_protocol" -> "smtp",
        "mail_debug" -> "true",
        "mail_from" -> "mfortin@m410.com"
//      "mail_user" -> "mf410",
//        "mail_store_protocol" -> "",
//        "mail_smpt_user" -> "",
      ))
    val service = new EmailService(config)
    service.send(Message(Array("michael@m410.us"),"Test Message","This is just a test"))
  }
}