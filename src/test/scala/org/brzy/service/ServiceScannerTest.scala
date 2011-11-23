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
package org.brzy.service

import org.junit.Test
import org.junit.Assert._
import org.scalatest.junit.JUnitSuite


class ServiceScannerTest extends JUnitSuite {

  val scanner = new ServiceScanner("org.brzy.webapp.mock")

  @Test
  def testServiceScanner() {
    val result = scanner.services
    assertNotNull(result)
    assertEquals(1,result.size)
  }
}