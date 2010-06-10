package org.brzy.scalate

import org.junit.Test
import org.junit.Assert._
import org.scalatest.junit.JUnitSuite

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
      "web_xml" -> List()
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
      "web_xml" -> List()
      ))
    val p3 = p1 << p2
    assertNotNull(p3)
    assertTrue(p3.repositories.isDefined)
    assertEquals(1,p3.repositories.get.size)
    p3.repositories.get.foreach(d => assertEquals("repo",d.id.get))
  }
}