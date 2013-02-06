package org.brzy.validator.constraints


import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers


class ValidateSizeSpec extends WordSpec with ShouldMatchers {

  "Validate Size" should {
    "validate" in {
      val size = Size(5 to 10)
      assert(size.isValid("hello you"))
    }
    "invaldiate" in {
      val size = Size(5 to 10)
      assert(!size.isValid("hello world"))
    }
    "validate number" in {
      val size = Size(5 to 10)
      assert(size.isValid(Integer.valueOf(6)))
    }
    "invalidate number" in {
      val size = Size(5 to 10)
      assert(!size.isValid(Integer.valueOf(26)))
    }
  }
}