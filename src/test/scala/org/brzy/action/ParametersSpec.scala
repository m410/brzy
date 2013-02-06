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
package org.brzy.action


import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers



class ParametersSpec extends WordSpec with ShouldMatchers with Fixtures {

  "Parameters" should {
    "return random types" in {
      assert(parameters2("id") != null)
      assert(parameters2("lastName") != null)
      assert(parameters2("firstName") != null)
      assert("12321".equalsIgnoreCase(parameters2("id")))
      assert(true == parameters2.url.contains("id"))
      assert("12321".equalsIgnoreCase(parameters2("id")))
      assert("thumb".equalsIgnoreCase(parameters2("lastName")))
      assert("john".equalsIgnoreCase(parameters2("firstName")))
      assert("yes".equalsIgnoreCase(parameters2("other")))
    }
  }
}