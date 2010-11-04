package org.brzy.validator

import constraints.{Size, NotNull}
import org.junit.Test
import org.junit.Assert._
import org.scalatest.junit.JUnitSuite
import java.lang.String


class ValidatorTest extends JUnitSuite {
  @Test def testValid = {
    val fixture = new ValidatorFixture("hasValue")
    val violations = Validator(fixture).check("toBeValidated", NotNull()).violations
    assertNull(violations.orNull)
  }

  @Test def testInvalid = {
    val fixture = new ValidatorFixture(null)
    val violations = Validator(fixture).check("toBeValidated", NotNull()).violations
    assertNotNull(violations.orNull)
    val violation = violations.get.iterator.next
    assertEquals("toBeValidated", violation.getPropertyPath.toString)
    assertEquals("Can not be null", violation.getMessage)
    assertNull(violation.getInvalidValue)
  }

  @Test def testInvalidSize = {
    val value: String = "An extra long value"
    val fixture = new ValidatorFixture(value)
    val violations = Validator(fixture).check("toBeValidated", Size(2 to 5)).violations
    assertNotNull(violations.orNull)
    val violation = violations.get.iterator.next
    assertEquals("toBeValidated", violation.getPropertyPath.toString)
    assertEquals(value, violation.getInvalidValue)
    assertEquals("Must be in range '2 - 5' ", violation.getMessage)
  }
}

case class ValidatorFixture(toBeValidated: String)