package org.brzy.cascal.mock

import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import org.brzy.cascal.{Cascal, CascalContextManager}
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
