package org.brzy.persistence.scalaJpa

import org.junit.Test
import org.junit.Ignore
import org.junit.Assert._
import org.brzy.mock.User
import org.brzy.action.args.Parameters
import collection.JavaConversions._

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class JpaPersistenceTest {

  @Test
  def testPersistenceMake ={
//    parameters.put("id",Array("1"))
    val map = new collection.mutable.HashMap[String, Array[String]]()
    map.put("name",Array("john"))
    map.put("submit",Array("Submit Save"))
    val parameters = new Parameters(map)
    val user = User.make(parameters)
    assertNotNull(user)
    assertEquals("john",user.name)
  }

  @Test
  @Ignore
  def testPersistenceValidate ={
    val map = new collection.mutable.HashMap[String, Array[String]]()
    map.put("id",Array("1"))
    map.put("name",Array("john"))
    val parameters = new Parameters(map)
    val user = new User
    val validity = user.validity()
    assertTrue(!validity.isValid)
  }
}