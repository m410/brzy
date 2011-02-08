package org.brzy.editor

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import java.util.Date
import java.lang.{Integer => JInt}

class BeanTest extends JUnitSuite {
  @Test def testConstructorBean = {
    val wrapper = Bean[CaseFixture](List(StringEditor("s", 0),
      DateEditor("d", "yyyy", 1),
      NumberEditor[JInt]("i", "0", 2),
      StringEditor("j", 3)))
    val fixture = wrapper.make(Map("s" -> "Hello", "d" -> "2019", "i" -> "114", "j" -> null))
    assertNotNull(fixture)
    assertNotNull(fixture.s)
    assertNotNull(fixture.d)
    assertNotNull(fixture.i)
    assertNull(fixture.j)
    assertEquals("Hello", fixture.s)
    assertEquals(114, fixture.i)
  }

  @Test def testVarBean = {
    val wrapper = Bean[VarFixture](List(StringEditor("s"),
      DateEditor("d", "yyyy"),
      NumberEditor[JInt]("i", "0"),
      StringEditor("j")))
    val fixture = wrapper.make(Map("s" -> "Hello", "d" -> "2019", "i" -> "114"))
    assertNotNull(fixture)
    assertNotNull(fixture.s)
    assertNotNull(fixture.d)
    assertNotNull(fixture.i)
    assertNull(fixture.j)
    assertEquals("Hello", fixture.s)
    assertEquals(114, fixture.i)
  }
}

case class CaseFixture(s: String, d: Date, i: JInt, j: String)

class VarFixture {
  var s: String = _
  var d: Date = _
  var i: JInt = _
  var j: String = _
}
