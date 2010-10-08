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
package org.brzy.mod.cascal.mock

import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import org.brzy.mod.cascal.{Cascal, CascalContextManager}
import com.shorrockin.cascal.utils.Conversions._
import org.junit.{Ignore, Test}


class PersonControllerTest extends JUnitSuite {
  // works but needs a running external database.
  @Test @Ignore def testGet = {
    val manager = new CascalContextManager
    val controller = new PersonController

    val setup = manager.createSession
    val keyId: String = "myKey"

    manager.context.withValue(setup) {
      val session = Cascal.value.get
      if (session.count("Keyspace1"\"Standard2"\keyId) == 0) {
        println("setup data in space")
        session.insert("Keyspace1"\"Standard2"\keyId\"firstName"\"Bob")
      }
    }
    manager.destroySession(setup)


    val some = manager.createSession
    manager.context.withValue(some) {
      val person = Person.get(keyId)
      assertNotNull(person)
      assertEquals("Bob", person.firstName)
    }
    manager.destroySession(some)
  }
}
