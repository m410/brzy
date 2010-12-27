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
import org.junit.{Ignore, Test}
import org.brzy.calista.SessionManager


class ConnectionTest extends JUnitSuite {
  // needs to have the database running to work
  @Test @Ignore def testConnect = {
    import org.brzy.calista.schema.Conversions._
    val manager = new SessionManager()

    manager.doWith { session =>
      assertEquals(0,session.count("Standard2"|"1"))
    }
  }
}