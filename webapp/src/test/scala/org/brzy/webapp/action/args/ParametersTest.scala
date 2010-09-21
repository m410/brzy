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
package org.brzy.webapp.action.args

import org.junit.Assert._
import org.junit.Test
import collection.JavaConversions._
import collection.immutable.HashMap
import org.scalatest.junit.JUnitSuite


class ParametersTest extends JUnitSuite {

  val map = new collection.mutable.HashMap[String, Array[String]]()
  map.put("lastName",Array("thumb"))
  map.put("firstName",Array("john"))
  map.put("other",Array("yes","no"))

  val jMap = new java.util.HashMap[String, Array[String]]()
  jMap.put("id",Array("12321"))
  val parameters = new Parameters(new JMapWrapper(jMap) ++ map)

  @Test def testMixedParams ={
    assertNotNull(parameters.get("id"))
    assertNotNull(parameters.get("lastName"))
    assertNotNull(parameters.get("firstName"))
    assertNotNull(parameters.get("randomName"))

    assertEquals(1,parameters.array("id").get.length)
    assertEquals("12321",parameters("id"))

    assertEquals(true,parameters.contains("id"))
    assertEquals("12321",parameters("id"))
    assertTrue(parameters.array("id").isDefined)
    assertEquals("12321",parameters.array("id").get(0))
    assertEquals("thumb",parameters("lastName"))
    assertEquals("thumb",parameters.array("lastName").get(0))
    assertEquals("john",parameters("firstName"))
    assertEquals("john",parameters.array("firstName").get(0))
    assertEquals("yes",parameters("other"))
    assertEquals("no",parameters.array("other").get(1))
    assertEquals(2,parameters.array("other").get.length)
  }

  @Test def testForeach = {
    var count = 0
    parameters.foreach(x => count = count+1)
    assertEquals(4,count)
  }

  @Test def testExists = {
    assertTrue(parameters.exists(p => p._1 == "id"))
  }

  @Test def testAddString = {
    val map = new java.util.HashMap[String, Array[String]]
    map.put("test",Array("value"))
    val parameters = new Parameters(map)
    assertNotNull(parameters)
    assertEquals(1,parameters.size)
    assertEquals("test",parameters.keysIterator.next)
    assertEquals("value",parameters("test"))
  }

  @Test def testIterate = {
    var called = false
    parameters.foreach(f => {
      if(f._1 == "id") {
        assertEquals("12321",f._2)
        called = true
      }
    })
    assertTrue(called)
  }
}