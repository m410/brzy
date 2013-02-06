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
package org.brzy


import org.brzy.application.WebApp
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.WordSpec

class BrzyFilterSpec extends WordSpec with ShouldMatchers with Fixtures {

  "Brzy Filter" should {
    "filter forward" in {

      val filter = new BrzyFilter
      filter.webapp = WebApp("test")
      filter.doFilter(request1,response1,chain1)
    }
    "filter pass through" in {

      val app = WebApp("test")
      app.actions.foreach(a => println("### action: " + a))
      assert(true != app.actions.isEmpty)

      val filter = new BrzyFilter
      filter.webapp = app
      filter.doFilter(request,response,chain)
    }
  }
}