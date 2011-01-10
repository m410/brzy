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
package org.brzy.mod.calista.mock

import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import org.junit.{Ignore, Test}
import org.brzy.mod.calista.{CalistaModConf, CalistaContextManager}
import org.brzy.calista.ocm.Calista
import java.util.UUID

class PersonControllerTest extends JUnitSuite {
  val keyId = UUID.randomUUID

  @Test @Ignore def testGet = {
    val manager = new CalistaContextManager(new CalistaModConf(Map("host"->"localhost")))
    val session = manager.createSession

    val controller = new PersonController

    val setup = manager.createSession


    manager.context.withValue(setup) {
      val session = Calista.value.get
      import org.brzy.calista.schema.Conversions._
      if (session.count("Standard2"|keyId) == 0) {
        println("setup data in space")
        session.insert("Standard2"|keyId|("firstName","Bob"))
      }
    }
    manager.destroySession(setup)


    val some = manager.createSession
    manager.context.withValue(some) {
      Person.get(keyId) match {
        case Some(person) =>
          assertNotNull(person)
          assertEquals("Bob", person.firstName)
        case _ => fail("No person found")
      }

    }
    manager.destroySession(some)
  }
}
