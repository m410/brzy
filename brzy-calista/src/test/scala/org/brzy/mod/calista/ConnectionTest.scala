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
package org.brzy.mod.calista

import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import com.shorrockin.calista.session._
import com.shorrockin.calista.utils.Conversions._
import org.junit.{Ignore, Test}


class ConnectionTest extends JUnitSuite {
  // needs to have the database running to work
  @Test @Ignore def testConnect = {
    val hosts = Host("localhost", 9160, 250) :: Nil
    val params = new PoolParams(10, ExhaustionPolicy.Fail, 500L, 6, 2)
    val pool = new SessionPool(hosts, params, Consistency.One)

    pool.borrow { session =>
      assertEquals(0,session.count("Keyspace1" \ "Standard2" \ "1"))
    }
  }
}