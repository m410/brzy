package org.brzy.validator

import constraints.{Size, NotNull}
import java.lang.String
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.WordSpec


class ValidatorSpec extends WordSpec with ShouldMatchers with Fixtures {

  "Validator" should {
    "mark valid" in {
      val fixture = new ValidatorFixture("hasValue")
      val violations = Validator(fixture).check("toBeValidated", NotNull()).violations
      assert(null == violations.orNull)
    }
    "mark invalid" in {
      val fixture = new ValidatorFixture(null)
      val violations = Validator(fixture).check("toBeValidated", NotNull()).violations
      assert(violations.orNull != null)
      val violation = violations.get.iterator.next()
      assert("toBeValidated".equalsIgnoreCase( violation.getPropertyPath.toString))
      assert("Can not be null".equalsIgnoreCase(violation.getMessage))
      assert(null == violation.getInvalidValue)
    }
    "mark invalid size" in {
      val value: String = "An extra long value"
      val fixture = new ValidatorFixture(value)
      val violations = Validator(fixture).check("toBeValidated", Size(2 to 5)).violations
      assert(violations.orNull != null)
      val violation = violations.get.iterator.next()
      assert("toBeValidated".equalsIgnoreCase( violation.getPropertyPath.toString))
      assert(value.equalsIgnoreCase(violation.getInvalidValue.toString))
      assert("Must be in range '2 - 5' ".equalsIgnoreCase(violation.getMessage))
    }
  }
}

