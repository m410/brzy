package org.brzy.scalate

import org.junit.Test
import org.junit.Assert._
import org.scalatest.junit.JUnitSuite
import org.brzy.config.plugin.Plugin

/**
 * Document Me..
 *
 * @author Michael Fortin
 * @version $Id : $
 */
class ScalatePluginConfigTest extends JUnitSuite {

  @Test
  def testMerge = {
    val p1 = new ScalatePluginConfig(Map[String, AnyRef](
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
    
    val p2 = new ScalatePluginConfig(Map[String, AnyRef](
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
    val merged = {p1 << p2}.asInstanceOf[ScalatePluginConfig]
    assertNotNull(merged)
    assertTrue(merged.repositories.isDefined)
    assertEquals(1,merged.repositories.get.size)
    merged.repositories.get.foreach(d => assertEquals("repo",d.id.get))

    assertEquals(2,merged.webXml.get.size)
  }


  @Test
  def testMergePlugin = {
    val plugin = new Plugin(Map[String, AnyRef](
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
    assertTrue(plugin.repositories.isDefined)
    assertEquals(1,plugin.repositories.get.size)
    plugin.repositories.get.foreach(d => assertEquals("repo",d.id.get))

    val scalate = new ScalatePluginConfig(Map[String, AnyRef](
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
    val merged = {scalate << plugin}.asInstanceOf[ScalatePluginConfig]
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
    val plugin = new Plugin(Map[String, AnyRef](
      "name" -> "test",
      "version" -> "test",
      "org" -> "test",
      "config_class" -> "test",
      "resource_class" -> "test",
      "file_extension" -> "test",
      "remote_location" -> "test"
      ))

    assertFalse(plugin.isInstanceOf[ScalatePluginConfig])
  }
}