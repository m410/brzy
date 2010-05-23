package org.brzy.config

import org.junit.Test
import org.junit.Assert._
import java.util.{ArrayList => JList, HashMap => JMap}

/**
 *
 * @author Michael Fortin
 * @version $Id : $
 */
class ConfigTest {
  class TestConfig(m: Map[String, AnyRef]) extends Config(m) {
    val configurationName = "Test Config"
    val asMap = null
    val name: Option[String] = m.get("name").asInstanceOf[Option[String]].orElse(Option(null))
    val application: Option[Application] = m.get("application") match {
      case s: Some[_] => Option(new Application(s.get.asInstanceOf[Map[String, String]]))
      case _ => Option(null)
    }
    val webXml: Option[List[Map[String, AnyRef]]] = m.get("web_xml").asInstanceOf[Option[List[Map[String, AnyRef]]]]
    val repositories: Option[List[Repository]] = m.get("repositories") match {
      case s: Some[List[Map[String, AnyRef]]] => Option(s.get.map(i => new Repository(i)).toList)
      case _ => Option(null)
    }
    val dependencies: Option[List[Dependency]] = m.get("dependencies") match {
      case s: Some[List[Map[String, AnyRef]]] => Option(s.get.map(i => new Dependency(i)).toList)
      case _ => Option(null)
    }
  }

  @Test
  def testBasicConfig = {
    val m = Map("name" -> "example")
    val testConfig = new TestConfig(m)
    assertEquals("example", testConfig.name)
    assertNotNull(testConfig.repositories)
    assertTrue(testConfig.repositories.isEmpty)
    assertNotNull(testConfig.dependencies)
    assertTrue(testConfig.dependencies.isEmpty)
    assertNotNull(testConfig.webXml)
    assertTrue(testConfig.webXml.isEmpty)
  }

  @Test
  def testConfigWithRepositories = {
    val jlist = new JList[JMap[String, JMap[String, _]]]
    val jmap = new JMap[String, JMap[String, _]]
    jlist.add(jmap)
    val m = Map("name" -> "example", "repositories" -> jlist)
    val testConfig = new TestConfig(m)
    assertEquals("example", testConfig.name)
    assertNotNull(testConfig.repositories)
    assertEquals(1, testConfig.repositories.size)
  }

  @Test
  def testConfigWithDependencies = {
    val jlist = new JList[JMap[String, JMap[String, _]]]
    val jmap = new JMap[String, JMap[String, _]]
    jlist.add(jmap)
    val m = Map("name" -> "example", "dependencies" -> jlist)
    val testConfig = new TestConfig(m)
    assertEquals("example", testConfig.name)
    assertNotNull(testConfig.dependencies)
    assertEquals(1, testConfig.dependencies.size)
  }

  @Test
  def testConfigWithWebXml = {
    val jlist = new JList[JMap[String, String]]
    val jmap = new JMap[String, String]
    jmap.put("description", "Name of Description")
    jlist.add(jmap)
    val m = Map("name" -> "example", "web_xml" -> jlist)
    val testConfig = new TestConfig(m)
    assertEquals("example", testConfig.name)
    assertNotNull(testConfig.webXml)
    assertEquals(1, testConfig.webXml.size)
  }

  @Test
  def testConfigWithApplication = {
    val jmap = new JMap[String, String]
    jmap.put("name", "application")
    val m = Map("name" -> "example", "application" -> jmap)
    val testConfig = new TestConfig(m)
    assertEquals("example", testConfig.name)
    assertNotNull(testConfig.application)
  }
}