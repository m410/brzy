package org.brzy.action.args


import org.junit.Assert._
import org.junit.Test
import scala.collection.JavaConversions._
import collection.immutable.HashMap

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class ParameterTest {

  @Test
  def testAddString = {
    val map = new java.util.HashMap[String, Array[String]]
    map.put("test",Array("value"))
    val parameters:Parameters = new Parameters()
    parameters ++ map
    assertNotNull(parameters)
    assertEquals(1,parameters.size)
    assertEquals("test",parameters.keysIterator.next)
    assertEquals("value",parameters("test")(0))
  }
}