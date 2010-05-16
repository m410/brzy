package org.brzy.config

import org.junit.Test
import org.junit.Assert._
import java.util.{ArrayList=>JList, HashMap=>JMap}

/**
 *
 * @author Michael Fortin
 * @version $Id: $
 */
class ConfigTest {

  class TestConfig(m:Map[String,AnyRef]) extends Config(m) {
      val configurationName = "Test Config"
      val printAsTable = ""
      val asMap = null
      val name = set[String](m.get("name"))
      val repositories = makeSeq[Repository](classOf[Repository],m.get("repositories"))
      val dependencies = makeSeq[Dependency](classOf[Dependency],m.get("dependencies"))
      val webXml = makeWebXml(m.get("web_xml"))
      val application = make[Application](classOf[Application], m.get("application"))
  }

  @Test
  def testBasicConfig = {
    val m = Map("name"->"example")
    val testConfig = new TestConfig(m)
    assertEquals("example",testConfig.name)
    assertNull(testConfig.repositories)
    assertNull(testConfig.dependencies)
    assertNull(testConfig.webXml)
  }

  @Test
  def testConfigWithRepositories = {
    val jlist = new JList[JMap[String,JMap[String,_]]]
    val jmap = new JMap[String,JMap[String,_]]
    jlist.add(jmap)
    val m = Map("name"->"example","repositories"->jlist)
    val testConfig = new TestConfig(m)
    assertEquals("example",testConfig.name)
    assertNotNull(testConfig.repositories)
    assertEquals(1,testConfig.repositories.size)
  }

  @Test
  def testConfigWithDependencies = {
    val jlist = new JList[JMap[String,JMap[String,_]]]
    val jmap = new JMap[String,JMap[String,_]]
    jlist.add(jmap)
    val m = Map("name"->"example","dependencies"->jlist)
    val testConfig = new TestConfig(m)
    assertEquals("example",testConfig.name)
    assertNotNull(testConfig.dependencies)
    assertEquals(1,testConfig.dependencies.size)
  }

  @Test
  def testConfigWithWebXml = {
    val jlist = new JList[JMap[String,String]]
    val jmap = new JMap[String,String]
    jmap.put("description","Name of Description")
    jlist.add(jmap)
    val m = Map("name"->"example","web_xml"->jlist)
    val testConfig = new TestConfig(m)
    assertEquals("example",testConfig.name)
    assertNotNull(testConfig.webXml)
    assertEquals(1,testConfig.webXml.size)
  }

  @Test
  def testConfigWithApplication = {
    val jmap = new JMap[String,String]
    jmap.put("name","application")
    val m = Map("name"->"example","application"->jmap)
    val testConfig = new TestConfig(m)
    assertEquals("example",testConfig.name)
    assertNotNull(testConfig.application)
  }
}