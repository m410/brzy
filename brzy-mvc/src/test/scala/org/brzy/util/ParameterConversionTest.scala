package org.brzy.util

import org.junit.Test
import org.junit.Assert._
import ParameterConversion._
import org.scalatest.junit.JUnitSuite


class ParameterConversionTest extends JUnitSuite {

  @Test
  def testToType = {
    assertEquals("SomeVal",toType(classOf[String],"SomeVal"))
    assertEquals(100L,toType(classOf[java.lang.Long],"100"))
  }
}