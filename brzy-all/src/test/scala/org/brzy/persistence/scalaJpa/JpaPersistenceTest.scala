package org.brzy.persistence.scalaJpa

import org.junit.Test
import org.junit.Assert._
import org.brzy.mock.User
import org.brzy.action.args.Parameters

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class JpaPersistenceTest {

  @Test
  def testPersistenceMake ={
    val parameters = new Parameters()
//    parameters.put("id",Array("1"))
    parameters.put("name",Array("john"))
    val user = User.make(parameters)
    assertNotNull(user)
    assertEquals("john",user.name)
  }

  @Test
  def testPersistenceValidate ={
    val parameters = new Parameters()
    parameters.put("id",Array("1"))
    parameters.put("name",Array("john"))
    val user = new User
    val validity = user.validity()
    validity.violations.foreach(println _)
    assertTrue(!validity.isValid)
  }
}