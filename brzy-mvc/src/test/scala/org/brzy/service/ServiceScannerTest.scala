package org.brzy.service

import org.junit.Test
import org.junit.Assert._
import org.scalatest.junit.JUnitSuite


class ServiceScannerTest extends JUnitSuite {

  val scanner = new ServiceScanner("org.brzy.mvc.mock")

  @Test
  def testServiceScanner = {
    val result = scanner.services
    assertNotNull(result)
    assertEquals(1,result.size)
  }
}