package org.brzy.validator.constraints

import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import org.brzy.validator.DefaultConstraintValidatorContext
import org.junit.{Ignore, Test}

class PatternTest extends JUnitSuite {

  @Test def testSizeValid = {
    val pattern = Pattern("hello")
    assertTrue(pattern.isValid("hello you", new DefaultConstraintValidatorContext))
  }

  @Test def testSizeInvalid = {
    val pattern = Pattern("hello")
    assertFalse(pattern.isValid(" world", new DefaultConstraintValidatorContext))
  }

}