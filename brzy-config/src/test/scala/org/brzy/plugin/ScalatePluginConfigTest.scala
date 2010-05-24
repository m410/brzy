package org.brzy.plugin

import org.junit.Test
import org.junit.Assert._

/**
 * Document Me..
 *
 * @author Michael Fortin
 * @version $Id : $
 */
class ScalatePluginConfigTest {
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
      "repositories" -> null,
      "dependencies" -> null,
      "web_xml" -> null
      ))
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
      "dependencies" -> null,
      "web_xml" -> null
      ))
    val p3 = p1 << p2
    assertNotNull(p3)
  }
}