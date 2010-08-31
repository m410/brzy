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
import org.junit.Test
import org.junit.Assert._
import java.beans.ConstructorProperties


class BuildTest extends JUnitSuite {
  @Test def testBuild = {
    val fixture = Build[BuildFixture]().arg("name"->"test").arg("num"->4.asInstanceOf[AnyRef]).make
    assertNotNull(fixture)
    assertEquals("test",fixture.name)
    assertEquals(4,fixture.num)
  }
}

@ConstructorProperties(Array("name","num"))
class BuildFixture(val name:String, val num:Int)