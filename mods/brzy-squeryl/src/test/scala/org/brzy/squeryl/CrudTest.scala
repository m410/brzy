package org.brzy.squeryl

import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import org.brzy.mock.Person
import org.brzy.mvc.action.args.Parameters
import org.junit.{Ignore, Test}


class CrudTest extends JUnitSuite {
  val ctx = new SquerylContextManager("org.h2.Driver", "jdbc:h2:squery-test", "sa", "")
//  val session = ctx.factory.create
//  ctx.context.withValue(session) {
//    Person.findTableFor(Person) match {
//      case Some(_) => //
//      case _ => Person.create
//    }
//  }
//  ctx.destroySession(session)
  
  @Test @Ignore def testCreate = {
    val session = ctx.createSession
    ctx.context.withValue(session) {
      def person = new Person(0, "one", "two")
      person.insert()
    }
    ctx.destroySession(session)
  }

  @Test @Ignore def testRead = {
    val session = ctx.createSession
    ctx.context.withValue(session) {
      assertNotNull(Person.get(1))
    }
    ctx.destroySession(session)
  }

  @Test @Ignore def testUpdate = {
    val session = ctx.createSession
    ctx.context.withValue(session) {
      assertNotNull(Person.get(1))
      val p2 = new Person(1, "update first", "update last")
      p2.update()
    }
    ctx.destroySession(session)
  }

  @Test @Ignore def testDelete = {
    val session = ctx.createSession
    ctx.context.withValue(session) {
      val person = Person.get(1)
      person.delete()
    }
    ctx.destroySession(session)
  }

  @Test @Ignore def testList = {
    val session = ctx.createSession
    ctx.context.withValue(session) {
      val persons = Person.list()
      assertNotNull(persons)
      assertEquals(1, persons.size)
    }
    ctx.destroySession(session)
  }

  @Test @Ignore def testMake = {
    val person = Person.make(Map("id" -> 1, "firstName" -> "fred", "lastName" -> "bob"))
    assertNotNull(person)
    assertEquals(1, person.id)
    assertEquals("fred", person.firstName)
    assertEquals("bob", person.lastName)
  }
}