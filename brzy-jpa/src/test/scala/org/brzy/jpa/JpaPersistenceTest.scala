package org.brzy.jpa

import org.junit.Test
import org.junit.Ignore
import org.junit.Assert._
import org.brzy.jpa.mock.User
import org.brzy.mvc.action.args.Parameters
import collection.JavaConversions._
import org.scalatest.junit.JUnitSuite


class JpaPersistenceTest extends JUnitSuite {

  @Test
  def testPersistenceMake ={
    val map = new collection.mutable.HashMap[String, Array[String]]()
    map.put("id",Array("1"))
    map.put("version",Array("1"))
    map.put("firstName",Array("john"))
    map.put("lastName",Array("Smith"))

    val user = User.make(new Parameters(map))
    assertNotNull(user)
    assertEquals("john",user.firstName)
  }

  @Test
  def testPersistenceValidate ={
    val map = new collection.mutable.HashMap[String, Array[String]]()
    map.put("id",Array("1"))
    map.put("version",Array("1"))
    map.put("lastName",Array("Smith"))
    map.put("firstName",Array("John"))
    val parameters = new Parameters(map)
    val user = new User
    val validity = user.validate()
    assertTrue(!validity.passes)
  }
}