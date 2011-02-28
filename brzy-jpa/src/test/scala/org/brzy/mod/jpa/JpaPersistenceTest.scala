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
package org.brzy.mod.jpa

import org.junit.Test
import org.junit.Assert._
import org.brzy.mod.jpa.mock.User
import collection.JavaConversions._
import org.scalatest.junit.JUnitSuite


class JpaPersistenceTest extends JUnitSuite {

  @Test def testPersistenceMake ={
    val map = new collection.mutable.HashMap[String, Array[String]]()
    map.put("id",Array("1"))
    map.put("version",Array("1"))
    map.put("firstName",Array("john"))
    map.put("lastName",Array("Smith"))

    val cmap:Map[String,Any] = map.map(kv=>{kv._1->kv._2(0)}).toMap
    val user = User.construct(cmap)
    assertNotNull(user)
    assertEquals("john",user.firstName)
  }

  @Test def testPersistenceValidate = {
    val user = new User
    val validity = user.validate()
    assertNotNull(validity.orNull)
  }
}