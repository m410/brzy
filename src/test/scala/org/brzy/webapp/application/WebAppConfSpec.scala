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
package org.brzy.webapp.application

import org.brzy.mock.MockSquerylModConfig
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.WordSpec
import org.brzy.webapp.application.WebAppConfiguration
import scala.Predef._


class WebAppConfSpec extends WordSpec with ShouldMatchers with Fixture {

  "WebAppConf" should {

    "create configuration" in {
      val wac = WebAppConfiguration.runtime(env = "test", defaultConfig = "/brzy-webapp.test.b.yml")
      assert(wac != null)
      assert(wac.application.isDefined)
      assert(wac.useSsl, "ssl should be true: "+wac.useSsl)
      assert(wac.build.isDefined)
      assert(wac.logging.isDefined)
      assert(wac.logging.get.loggers != null)
      assert(wac.logging.get.appenders != null)
      assert(wac.logging.get.root != null)
      assert(1 == wac.logging.get.root.get.ref.size)
      assert(wac.dependencies != null)
      assert(18 == wac.dependencies.size, s"expected 18, was ${wac.dependencies.size}")
      assert(wac.dependencyExcludes != null)
      assert(0 == wac.dependencyExcludes.size)
      assert(wac.repositories != null)
      assert(8 == wac.repositories.size)
      assert(0 == wac.modules.size)
      assert(1 == wac.persistence.size)

      val squeryl = wac.persistence.find(_.name.get == "brzy-squeryl")
      assert(squeryl.isDefined)
      assert(squeryl.get.isInstanceOf[MockSquerylModConfig])
      val squerylConfig = squeryl.get.asInstanceOf[MockSquerylModConfig]
      assert(squerylConfig.driver.isDefined)
      assert("org.h2.Driver".equalsIgnoreCase(squerylConfig.driver.get))

      assert(wac.webXml != null)
      assert(wac.environment != null)
      assert("test".equalsIgnoreCase(wac.environment))
      assert(9 == wac.webXml.size)

      assert(wac.views.isDefined)
      assert(2 == wac.views.get.webXml.size)

      assert(1 == wac.logging.get.root.get.ref.size)
      assert("STDOUT".equalsIgnoreCase( wac.logging.get.root.get.ref.get(0)))
    }

    "create json" in {
      val wac = WebAppConfiguration.runtime(env = "test", defaultConfig = "/brzy-webapp.test.b.yml")
      val json = wac.toJson
      assert(json != null)
      val wacFromJson = WebAppConfiguration.fromJson(json)
      assert(wacFromJson != null)

      assert(wacFromJson.application.isDefined)
      assert(wacFromJson.build.isDefined)
      assert(wacFromJson.logging.isDefined)
      assert(wacFromJson.logging.get.loggers.isDefined)
      assert(wacFromJson.logging.get.appenders.isDefined)
      assert(wacFromJson.logging.get.root.isDefined)
      assert(1 == wacFromJson.logging.get.root.get.ref.size)
      assert(wacFromJson.dependencies != null)
      assert(18 == wacFromJson.dependencies.size, s"expected 18, was ${wac.dependencies.size}")
      assert(wacFromJson.dependencyExcludes != null)
      assert(0 == wacFromJson.dependencyExcludes.size)
      assert(wacFromJson.repositories != null)
      assert(8 == wacFromJson.repositories.size)
      assert(0 == wac.modules.size)
      assert(1 == wac.persistence.size)
      assert(wacFromJson.webXml != null)
      assert(wacFromJson.environment != null)
      assert("test".equalsIgnoreCase(wac.environment))
      assert(9 == wacFromJson.webXml.size)

      assert(1 == wacFromJson.logging.get.root.get.ref.size)
      assert("STDOUT".equalsIgnoreCase( wacFromJson.logging.get.root.get.ref.get(0)))
    }
  }
}
