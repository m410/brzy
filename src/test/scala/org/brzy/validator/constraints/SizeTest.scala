package org.brzy.validator.constraints

import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import org.brzy.validator.DefaultConstraintValidatorContext
import org.junit.{Ignore, Test}

class SizeTest extends JUnitSuite {

  @Test def testSizeValid = {
    val size = Size(5 to 10)
    assertTrue(size.isValid("hello you", new DefaultConstraintValidatorContext))
  }

  @Test def testSizeInvalid = {
  val size = Size(5 to 10)
    assertFalse(size.isValid("hello world", new DefaultConstraintValidatorContext))
  }

  @Ignore @Test def testNumberSizeValid = {
    val size = Size(5 to 10)
    assertTrue(size.isValid(Integer.valueOf(6), new DefaultConstraintValidatorContext))
  }

  @Ignore @Test def testNumberSizeInvalid = {
  val size = Size(5 to 10)
    assertFalse(size.isValid(Integer.valueOf(26), new DefaultConstraintValidatorContext))
  }
}