package org.brzy.service

import org.junit.Test
import org.junit.Assert._

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */

class BrzyServiceScannerTest {

  val scanner = new BrzyServiceScanner("org.brzy.mock")

  @Test
  def testServiceScanner = {
    val result = scanner.services
    assertNotNull(result)
    assertEquals(3,result.size)
  }
}