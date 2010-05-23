package org.brzy.controller

import org.junit.Test
import org.junit.Ignore
import org.junit.Assert._
import org.reflections.scanners.{ResourcesScanner, TypeAnnotationsScanner, SubTypesScanner}
import org.reflections.util.{ConfigurationBuilder, ClasspathHelper}

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
class BrzyControllerScannerTest {

  val scanner = new ControllerScanner("org.brzy.mock")

  @Test
  @Ignore
  def testControllerScanner = {
    val result = scanner.controllers
    assertNotNull(result)
    assertEquals(2,result.size)
  }

}