package org.brzy.bean

import org.scalatest.junit.JUnitSuite
import java.beans.ConstructorProperties
import org.junit.Test
import org.junit.Assert._


class ConstructTest extends JUnitSuite {
  @Test def testCreate = {
    val fixture = Construct[Fixture](Map("x" -> "x", "z" -> "z", "y" -> "y"))
    assertNotNull(fixture)
    assertEquals("x", fixture.x)
    assertEquals("y", fixture.y)
    assertEquals("z", fixture.z)
  }
}


@ConstructorProperties(Array("x", "y", "z"))
case class Fixture(x: String, y: String, z: String)