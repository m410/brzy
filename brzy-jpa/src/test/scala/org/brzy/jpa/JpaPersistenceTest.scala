/*
 * Copyright 2010 Michael Fortin <mike@brzy.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
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