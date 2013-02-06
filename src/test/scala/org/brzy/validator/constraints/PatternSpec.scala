package org.brzy.validator.constraints

import org.scalatest.junit.JUnitSuite

import org.junit.{Ignore, Test}
import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers

class PatternSpec extends WordSpec with ShouldMatchers {

  "Pattern validator" should {
    "validate" in {
      val pattern = Pattern("hello")
      assert(true == pattern.isValid("hello you"))
    }
    "not validate" in {
      val pattern = Pattern("hello")
      assert(!pattern.isValid(" world"))
    }
  }
}