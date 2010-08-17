package org.brzy.reflect

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._


class PropertiesTest extends JUnitSuite {

  @Test def testAccess = {
    import Properties._
    val simple = new SimpleFixture
    simple.properties.keys.foreach(println(_))
    assertEquals("Test", simple.properties("name"))
  }
}

class SimpleFixture {
  val name = "Test"
}