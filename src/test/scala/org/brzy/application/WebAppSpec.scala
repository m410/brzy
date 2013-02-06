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
package org.brzy.application



import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers


class WebAppSpec extends WordSpec with ShouldMatchers  with Fixture {

  "WebApp" should {
    "create webapp" in {
      val webappConf = WebAppConfiguration.runtime(env="test",defaultConfig="/brzy-webapp.test.b.yml")
      assert(webappConf != null)

      val webapp = WebApp(webappConf)
      assert(webapp != null)

      assert(webapp.services != null)
      assert(1 == webapp.services.size)

      assert(webapp.controllers != null)
      assert(2 == webapp.controllers.size)

      assert(webapp.actions != null)
      assert(19 == webapp.actions.size)
    }
    "call doFilter and return dispath to servlet" in {

    }
    "call doFilter and return dispath to async servlet" in {

    }
    "call doFilter and return redirect to ssl" in {

    }
    "call doFilter and return redirect to authenticate" in {

    }
    "call doFilter and return action" in {

    }
    "call doFilter and return async action" in {

    }
  }
}