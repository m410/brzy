package org.brzy.mvc.action.args


import org.junit.Assert._
import org.junit.Test
import collection.JavaConversions._
import collection.immutable.HashMap
import org.scalatest.junit.JUnitSuite


class ParametersTest extends JUnitSuite {

  val map = new collection.mutable.HashMap[String, Array[String]]()
  map.put("lastName",Array("thumb"))
  map.put("firstName",Array("john"))

  val jMap = new java.util.HashMap[String, Array[String]]()
  jMap.put("id",Array("12321"))
  val parameters = new Parameters(new JMapWrapper(jMap) ++ map)

  @Test
  def testAddString = {
    val map = new java.util.HashMap[String, Array[String]]
    map.put("test",Array("value"))
    val parameters = new Parameters(new JMapWrapper[String, Array[String]](map))
    assertNotNull(parameters)
    assertEquals(1,parameters.size)
    assertEquals("test",parameters.keysIterator.next)
    assertEquals("value",parameters("test")(0))
  }

  @Test
  def testMixedParams ={
    assertNotNull(parameters.get("id"))
    assertNotNull(parameters.get("lastName"))
    assertNotNull(parameters.get("firstName"))
    assertNotNull(parameters.get("randomName"))

    assertEquals(1,parameters.get("id").get.length)
    assertEquals("12321",parameters.get("id").get(0))

    assertEquals("12321",parameters.str("id").get)
    assertEquals("thumb",parameters.str("lastName").get)
    assertEquals("john",parameters.str("firstName").get)
  }

  @Test
  def testForeach = {
    var count = 0
    parameters.foreach(x => count = count+1)
    assertTrue(count == 3)
  }

  @Test
  def testExists = {
    assertTrue(parameters.exists(p => p._1 == "id"))
  }
}