package org.brzy.scalate

import org.junit.Test
import org.junit.Assert._
import org.scalatest.junit.JUnitSuite
import org.brzy.config.mod.Mod

/**
 * Document Me..
 *
 * @author Michael Fortin
 * @version $Id : $
 */
class ScalateModConfigTest extends JUnitSuite {

  @Test
  def testMerge = {
    val p1 = new ScalateModConfig(Map[String, AnyRef](
      "name" -> "test",
      "version" -> "test",
      "org" -> "test",
      "config_class" -> "test",
      "resource_class" -> "test",
      "file_extension" -> "test",
      "remote_location" -> "test",
      "local_location" -> "test",
      "repositories" -> List(Map("url"->"http://someurl.com","id"->"repo")),
      "dependencies" -> List(),
      "web_xml" -> List(Map("display-name"->"brzy servlet"))
      ))
    assertTrue(p1.repositories.isDefined)
    assertEquals(1,p1.repositories.get.size)
    p1.repositories.get.foreach(d => assertEquals("repo",d.id.get))
    
    val p2 = new ScalateModConfig(Map[String, AnyRef](
      "name" -> "test2",
      "version" -> "test2",
      "org" -> "test2",
      "config_class" -> "test2",
      "resource_class" -> "test2",
      "file_extension" -> "test2",
      "remote_location" -> "test2",
      "local_location" -> "test2",
      "repositories" -> null,
      "dependencies" -> List(),
      "web_xml" -> List(Map("description"->"description goes here"))
      ))
    val merged = {p1 << p2}.asInstanceOf[ScalateModConfig]
    assertNotNull(merged)
    assertTrue(merged.repositories.isDefined)
    assertEquals(1,merged.repositories.get.size)
    merged.repositories.get.foreach(d => assertEquals("repo",d.id.get))

    assertEquals(2,merged.webXml.get.size)
  }


  @Test
  def testMergeModule = {
    val mod = new Mod(Map[String, AnyRef](
      "name" -> "test",
      "version" -> "test",
      "org" -> "test",
      "config_class" -> "test",
      "resource_class" -> "test",
      "file_extension" -> "test",
      "remote_location" -> "test",
      "local_location" -> "test",
      "repositories" -> List(Map("url"->"http://someurl.com","id"->"repo")),
      "dependencies" -> List(),
      "web_xml" -> List(Map("display-name"->"brzy servlet"))
      ))
    assertTrue(mod.repositories.isDefined)
    assertEquals(1,mod.repositories.get.size)
    mod.repositories.get.foreach(d => assertEquals("repo",d.id.get))

    val scalate = new ScalateModConfig(Map[String, AnyRef](
      "name" -> "test2",
      "version" -> "test2",
      "org" -> "test2",
      "config_class" -> "test2",
      "resource_class" -> "test2",
      "file_extension" -> "test2",
      "remote_location" -> "test2",
      "local_location" -> "test2",
      "repositories" -> null,
      "dependencies" -> List(),
      "web_xml" -> List(Map("description"->"test deesc"))
      ))
    val merged = {scalate << mod}.asInstanceOf[ScalateModConfig]
    assertNotNull(merged)
    assertTrue(merged.repositories.isDefined)
    assertEquals(1,merged.repositories.get.size)
    merged.repositories.get.foreach(d => assertEquals("repo",d.id.get))

    assertTrue(merged.fileExtension.isDefined)
    assertEquals("test",merged.fileExtension.get)

    assertEquals(2,merged.webXml.get.size)
  }

  @Test
  def testInstanceOf = {
    val plugin = new Mod(Map[String, AnyRef](
      "name" -> "test",
      "version" -> "test",
      "org" -> "test",
      "config_class" -> "test",
      "resource_class" -> "test",
      "file_extension" -> "test",
      "remote_location" -> "test"
      ))

    assertFalse(plugin.isInstanceOf[ScalateModConfig])
  }
}