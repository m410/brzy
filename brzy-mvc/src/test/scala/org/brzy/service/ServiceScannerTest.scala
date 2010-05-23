package org.brzy.service

import org.junit.Test
import org.junit.Assert._

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */

class ServiceScannerTest {

  val scanner = new ServiceScanner("org.brzy.mock")

  @Test
  def testServiceScanner = {
    val result = scanner.services
    assertNotNull(result)
    assertEquals(1,result.size)
  }
}