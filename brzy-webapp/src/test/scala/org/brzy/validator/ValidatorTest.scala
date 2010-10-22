package org.brzy.validator

import constraints.NotNull
import org.junit.Test
import org.junit.Assert._
import org.scalatest.junit.JUnitSuite


class ValidatorTest extends JUnitSuite {

  @Test def testValid = {
    val fixture = new ValidatorFixture("hasValue")
    val violations= Validator(fixture).check("toBeValidated", NotNull).violations
    assertNull(violations.orNull)
  }

  @Test def testInvalid= {
    val fixture = new ValidatorFixture(null)
    val violations= Validator(fixture).check("toBeValidated", NotNull).violations
    assertNotNull(violations.orNull)
  }
}

case class ValidatorFixture (toBeValidated:String)