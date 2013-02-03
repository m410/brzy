package org.brzy.action

/*
 * Copyright 2010 Michael Fortin <mike@brzy.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

import args.{ParametersRequest, Parameters}
import org.junit.Assert._
import org.junit.Test
import org.scalatest.junit.JUnitSuite
import org.springframework.mock.web.{MockServletContext, MockHttpServletRequest}
import collection.JavaConversions._
import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers

class ParametersSpec extends WordSpec with ShouldMatchers with Fixtures {

  "Parameters" should {
    "return random types" in {
      assertNotNull(parameters2("id"))
      assertNotNull(parameters2("lastName"))
      assertNotNull(parameters2("firstName"))
      assertEquals("12321",parameters2("id"))
      assertEquals(true,parameters2.url.contains("id"))
      assertEquals("12321",parameters2("id"))
      assertEquals("thumb",parameters2("lastName"))
      assertEquals("john",parameters2("firstName"))
      assertEquals("yes",parameters2("other"))
    }
  }
}