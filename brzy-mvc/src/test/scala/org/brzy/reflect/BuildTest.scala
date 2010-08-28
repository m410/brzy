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