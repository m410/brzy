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