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

  val scanner = new BrzyControllerScanner("org.brzy.mock")

  @Test
  @Ignore
  def testControllerScanner = {
    val result = scanner.controllers
    assertNotNull(result)
    assertEquals(2,result.size)
  }

  @Test
  @Ignore
	def testActionMap = {
    val result = scanner.actionMapping
    assertNotNull(result)
    assertEquals(14,result.size)
    assertTrue(result.exists(i=> {i.path == "users/create"}))
  }
}