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
package org.brzy.reflect

import org.scalatest.junit.JUnitSuite
import java.beans.ConstructorProperties
import org.junit.Test
import org.junit.Assert._


class ConstructTest extends JUnitSuite {
  @Test def testCreateWithMap = {
    val fixture = Construct[Fixture](Map("x" -> "x", "z" -> "z", "y" -> "y"))
    assertNotNull(fixture)
    assertEquals("x", fixture.x)
    assertEquals("y", fixture.y)
    assertEquals("z", fixture.z)
  }

  @Test def testCreateWithArray = {
    val fixture = Construct[Fixture](Array("x","y","z"))
    assertNotNull(fixture)
    assertEquals("x", fixture.x)
    assertEquals("y", fixture.y)
    assertEquals("z", fixture.z)
  }

  @Test def createNoArg = {
    assertNotNull(Construct[Fixture2]())
  }
  @Test def createWithStr = {
    assertNotNull(Construct[Fixture]("org.brzy.reflect.Fixture",Array("x","y","z")))
  }

  @Test def createWithCast = {
    val fixture = Construct.withCast[Fixture3](Map("y" -> "10", "x" -> "x"))
    assertNotNull(fixture)
    assertEquals(10,fixture.y)
  }
}


@ConstructorProperties(Array("x", "y", "z"))
case class Fixture(x: String, y: String, z: String)


class Fixture2()

@ConstructorProperties(Array("x", "y"))
case class Fixture3(x: String, y: Int)