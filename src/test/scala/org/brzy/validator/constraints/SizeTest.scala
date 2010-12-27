package org.brzy.validator.constraints

import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import org.junit.{Ignore, Test}

class SizeTest extends JUnitSuite {

  @Test def testSizeValid = {
    val size = Size(5 to 10)
    assertTrue(size.isValid("hello you"))
  }

  @Test def testSizeInvalid = {
  val size = Size(5 to 10)
    assertFalse(size.isValid("hello world"))
  }

  @Ignore @Test def testNumberSizeValid = {
    val size = Size(5 to 10)
    assertTrue(size.isValid(Integer.valueOf(6)))
  }

  @Ignore @Test def testNumberSizeInvalid = {
  val size = Size(5 to 10)
    assertFalse(size.isValid(Integer.valueOf(26)))
  }
}