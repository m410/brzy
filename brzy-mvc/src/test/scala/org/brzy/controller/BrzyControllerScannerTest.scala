package org.brzy.controller

import org.junit.Test
import org.junit.Ignore
import org.junit.Assert._
import org.scalatest.junit.JUnitSuite

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
class BrzyControllerScannerTest extends JUnitSuite {

  val scanner = new ControllerScanner("org.brzy.mock")

  @Test
  def testControllerScanner = {
    val result = scanner.controllers
    assertNotNull(result)
    assertEquals(1,result.size)
  }

}