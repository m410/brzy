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
package org.brzy.config.common

import org.junit.Test
import org.junit.Assert._
import org.scalatest.junit.JUnitSuite


class ProjectTest extends JUnitSuite {

  @Test
  def testMerge = {
    val map1 = Map[String,String](
    "scala_version" -> "one",
      "sbt_version" -> "three",
      "package_type" -> "four",
      "module_resources" -> "five",
      "module_repository" -> "six"
      )
    val project = new Project(map1)
    assertEquals("one",project.scalaVersion.get)
    assertEquals("three",project.sbtVersion.get)
    assertEquals("four",project.packageType.get)
    assertEquals("five",project.moduleResources.get)
    assertEquals("six",project.moduleRepository.get)

    val map2 = Map[String,String](
    "scala_version" -> "seven",
      "sbt_version" -> "nine",
      "package_type" -> "ten",
      "module_resources" -> "eleven",
      "module_repository" -> "twelve")
    val project1 = new Project(map2)
    val merge = project << project1
    assertNotNull(merge)
    assertEquals("seven",merge.scalaVersion.get)
    assertEquals("nine",merge.sbtVersion.get)
    assertEquals("ten",merge.packageType.get)
    assertEquals("eleven",merge.moduleResources.get)
    assertEquals("twelve",merge.moduleRepository.get)
  }
}