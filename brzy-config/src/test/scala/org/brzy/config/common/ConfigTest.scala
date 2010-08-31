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
import java.util.{ArrayList => JList, HashMap => JMap}
import org.scalatest.junit.JUnitSuite


class ConfigTest extends JUnitSuite {

  class TestConfig(m: Map[String, AnyRef]) extends Config(m) {
    val configurationName = "Test Config"
    val asMap = null
    val name: Option[String] = m.get("name").asInstanceOf[Option[String]].orElse(Option(null))
    val application: Option[Application] = m.get("application") match {
      case s: Some[_] => Option(new Application(s.get.asInstanceOf[Map[String, String]]))
      case _ => None
    }
    val webXml: Option[List[Map[String, AnyRef]]] = m.get("web_xml").asInstanceOf[Option[List[Map[String, AnyRef]]]]
    val repositories: Option[List[Repository]] = m.get("repositories") match {
      case Some(s) => Option(s.asInstanceOf[List[Map[String, AnyRef]]].map(i => new Repository(i)).toList)
      case _ => None
    }
    val dependencies: Option[List[Dependency]] = m.get("dependencies") match {
      case Some(s) => Option(s.asInstanceOf[List[Map[String, AnyRef]]].map(i => new Dependency(i)).toList)
      case _ => None
    }
  }

  @Test
  def testBasicConfig = {
    val m = Map("name" -> "example")
    val testConfig = new TestConfig(m)
    assertEquals("example", testConfig.name.get)
    assertNotNull(testConfig.repositories)
    assertTrue(testConfig.repositories.isEmpty)
    assertNotNull(testConfig.dependencies)
    assertTrue(testConfig.dependencies.isEmpty)
    assertNotNull(testConfig.webXml)
    assertTrue(testConfig.webXml.isEmpty)
  }

  @Test
  def testConfigWithRepositories = {
    val jlist = List[Map[String, Map[String, _]]](Map[String, Map[String, AnyRef]]())
    val m = Map("name" -> "example", "repositories" -> jlist)
    val testConfig = new TestConfig(m)
    assertEquals("example", testConfig.name.get)
    assertNotNull(testConfig.repositories.get)
    assertEquals(1, testConfig.repositories.get.size)
  }

  @Test
  def testConfigWithDependencies = {
    val jlist = List[Map[String, Map[String, AnyRef]]](Map[String, Map[String, AnyRef]]())
    val m = Map("name" -> "example", "dependencies" -> jlist)
    val testConfig = new TestConfig(m)
    assertEquals("example", testConfig.name.get)
    assertNotNull(testConfig.dependencies.get)
    assertEquals(1, testConfig.dependencies.get.size)
  }

  @Test
  def testConfigWithWebXml = {
    val jlist = List[Map[String, String]](Map[String, String]("description" -> "Name of Description"))
    val m = Map("name" -> "example", "web_xml" -> jlist)
    val testConfig = new TestConfig(m)
    assertEquals("example", testConfig.name.get)
    assertNotNull(testConfig.webXml.get)
    assertEquals(1, testConfig.webXml.get.size)
  }

  @Test
  def testConfigWithApplication = {
    val jmap = Map[String, String]("name" -> "application")
    val m = Map("name" -> "example", "application" -> jmap)
    val testConfig = new TestConfig(m)
    assertEquals("example", testConfig.name.get)
    assertNotNull(testConfig.application.get)
  }
}