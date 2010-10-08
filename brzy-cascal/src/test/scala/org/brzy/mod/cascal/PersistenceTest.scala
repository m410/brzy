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
package org.brzy.mod.cascal

import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import com.shorrockin.cascal.serialization.Converter
import com.shorrockin.cascal.session.Insert
import com.shorrockin.cascal.utils.Conversions._
import com.shorrockin.cascal.serialization.annotations._
import collection.mutable.{ListBuffer, ArrayBuffer}
import java.lang.reflect.Method
import java.util.{Arrays, UUID, Date}
//import org.apache.cassandra.service.EmbeddedCassandraService
import org.junit._

class PersistenceTest extends JUnitSuite {
  val key = "9fb38a59-c239-44a0-9d8e-7fa09044823b"
  val insertKey = "9fb38a59-c239-44a0-9d8e-7fa090448333"
//  var thread:Thread = _

//  @Before def startup = {
//    println("### startup")
//    System.setProperty("storage-config", "cassandra")
//    val cassandra = new EmbeddedCassandraService
//    cassandra.init
//    thread = new Thread(cassandra)
//    thread.setDaemon(true)
//    thread.start()
//  }
//
//  @After def shutdown = {
//    println("### shutdown")
//
//  }

  @Test @Ignore def testGetObject = {
    val manager = new CascalContextManager
    val session = manager.createSession

    manager.context.withValue(session) {
      val s = Cascal.value.get
      
      if (s.count(Person.keyspace \ Person.family \ key) < 3) {
        val firstColumn = Insert(Person.keyspace \ Person.family \ key \ "firstName" \ "Fred")
        val lastColumn = Insert(Person.keyspace \ Person.family \ key \ "lastName" \ "Doe")
        val createdColumn = Insert(Person.keyspace \ Person.family \ key \ "created" \ new Date)
        s.batch(firstColumn :: lastColumn :: createdColumn)
      }
      val person = Person.get(key)
      assertNotNull(person)
    }
    manager.destroySession(session)
  }

  @Test @Ignore def testSaveObject = {
    val manager = new CascalContextManager

    // check if it's in from a previous test
    val session = manager.createSession
    manager.context.withValue(session) {
      val s = Cascal.value.get

      if(s.count(Person.keyspace \ Person.family \ insertKey) > 1) {
        s.remove(Person.keyspace \ Person.family \ insertKey)
        println("removed key")
      }
    }
    manager.destroySession(session)

    // do the test
    val session2 = manager.createSession
    manager.context.withValue(session2) {
      val s = Cascal.value.get
      assertTrue(s.count(Person.keyspace \ Person.family \ insertKey) == 0)
      val person = Person(insertKey, "Fred", "Smith", new Date)
      Person.insert(person)
    }
    manager.destroySession(session2)

    // check to see if it's there
    val session3 = manager.createSession
    manager.context.withValue(session3) {
      val s = Cascal.value.get
      val count = s.count(Person.keyspace \ Person.family \ insertKey)
      println("count=" + count)
      assertTrue(count == 3) // todo ????
    }
    manager.destroySession(session3)
  }

  @Test @Ignore def countObject = {
    val manager = new CascalContextManager
    val session = manager.createSession
    manager.context.withValue(session) {
      val count = Person.count(insertKey)
      assertNotNull(count)
      assertEquals(3,count) //?
    }
    manager.destroySession(session)
  }
}

@Keyspace("Keyspace1")
@Family("Standard2")
case class Person(@Key val id: UUID,
    @Value("firstName") val firstName: String,
    @Value("lastName") val lastName: String,
    @Value("created") val created: Date)

object Person {
  val keyspace: String = "Keyspace1"
  val family: String = "Standard2"

  def get(key: String): Person = Converter[Person](Cascal.value.get.list(keyspace \ family \ key))

  def insert(person: Person) = {
    val buffer = new ListBuffer[Insert]()
    val key = person.id
    buffer += Insert(keyspace \ family \ key \ "firstName" \ person.firstName)
    buffer += Insert(keyspace \ family \ key \ "lastName" \ person.lastName)
    buffer += Insert(keyspace \ family \ key \ "created" \ person.created)
    Cascal.value.get.batch(buffer.toSeq)
  }

  def count(key:String) = Cascal.value.get.count(Person.keyspace \ Person.family \ key)
  
}