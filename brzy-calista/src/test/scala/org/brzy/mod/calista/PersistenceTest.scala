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
package org.brzy.mod.calista

import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import org.junit._

import java.util.{UUID, Date}
import org.brzy.calista.ocm.{KeyedEntity, ColumnMapping, Dao, Attribute,Calista}
import org.brzy.calista.serializer.{DateType, UuidType, Utf8Type}

class PersistenceTest extends JUnitSuite {
  val key = UUID.randomUUID
  val insertKey = UUID.randomUUID
  import org.brzy.calista.schema.Conversions._

//
//  @Test @Ignore def testGetObject = {
//    val manager = new CalistaContextManager(new CalistaModConf(Map("host"->"localhost")))
//    val session = manager.createSession
//    manager.context.withValue(session) {
//      val s = Calista.value.get
//
//      if (s.count(Person.family | key) < 3) {
//        val firstColumn = Insert(Person.family | key |("firstName" , "Fred"))
//        val lastColumn = Insert(Person.family | key |("lastName", "Doe"))
//        val createdColumn = Insert(Person.family | key |("created", new Date))
//        s.batch(firstColumn :: lastColumn :: createdColumn)
//      }
//      val person = Person.get(key)
//      assertNotNull(person)
//    }
//    manager.destroySession(session)
//  }

  @Test @Ignore def testSaveObject = {
    val manager = new CalistaContextManager(new CalistaModConf(Map("host"->"localhost")))
    val session = manager.createSession
    manager.context.withValue(session) {
      val s = Calista.value.get

      if(s.count(Person.family | insertKey) > 1) {
        s.remove(Person.family | insertKey)
        println("removed key")
      }
    }
    manager.destroySession(session)

    // do the test
    val session2 = manager.createSession
    manager.context.withValue(session2) {
      val s = Calista.value.get
      assertTrue(s.count(Person.family | insertKey) == 0)
      val person = Person(insertKey, "Fred", "Smith", new Date)
      person.save
    }
    manager.destroySession(session2)

    // check to see if it's there
    val session3 = manager.createSession
    manager.context.withValue(session3) {
      val s = Calista.value.get
      val count = s.count(Person.family | insertKey)
      println("count=" + count)
      assertTrue(count == 3) // todo ????
    }
    manager.destroySession(session3)
  }

//  @Test @Ignore def countObject = {
//    val manager = new CalistaContextManager(new CalistaModConf(Map("host"->"localhost")))
//    val session = manager.createSession
//
//    manager.context.withValue(session) {
//      val count = Person.count(insertKey)
//      assertNotNull(count)
//      assertEquals(3,count) //?
//    }
//    manager.destroySession(session)
//  }
}

case class Person(key: UUID, firstName: String, lastName: String, created: Date)
        extends KeyedEntity[UUID]

object Person extends Dao[UUID,Person]{
  val columnMapping = new ColumnMapping[Person]()
      .attributes(Utf8Type,Array(
        Attribute("key",true, UuidType),
        Attribute("firstName"),
        Attribute("lastName"),
        Attribute("created",false,DateType)))
  val family = columnMapping.family
}