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
package org.brzy.mod.scalate

import org.junit.Test
import org.junit.Assert._
import org.scalatest.junit.JUnitSuite
import org.brzy.fab.mod.{ViewMod, Mod}

class ScalateModConfigTest extends JUnitSuite {

  @Test
  def testMerge() {
    val p1 = new ScalateModConfig(Map[String, AnyRef](
      "name" -> "test",
      "version" -> "test",
      "org" -> "test",
      "config_class" -> "test",
      "provider_class" -> "test",
      "file_extension" -> "test",
      "remote_location" -> "test",
      "local_location" -> "test",
      "repositories" -> List(Map("url"->"http://someurl.com","id"->"repo")),
      "dependencies" -> List(),
      "web_xml" -> List(Map("display-name"->"brzy servlet"))
      ))
    assertEquals(1,p1.repositories.size)
    p1.repositories.foreach(d => assertEquals("repo",d.id.get))
    
    val p2 = new ScalateModConfig(Map[String, AnyRef](
      "name" -> "test2",
      "version" -> "test2",
      "org" -> "test2",
      "config_class" -> "test2",
      "provider_class" -> "test2",
      "file_extension" -> "test2",
      "remote_location" -> "test2",
      "local_location" -> "test2",
      "repositories" -> null,
      "dependencies" -> List(),
      "web_xml" -> List(Map("description"->"description goes here"))
      ))
    val merged = {p1 << p2}.asInstanceOf[ScalateModConfig]
    assertNotNull(merged)
    assertEquals(1,merged.repositories.size)
    merged.repositories.foreach(d => assertEquals("repo",d.id.get))

    assertEquals(2,merged.webXml.size)
  }


  @Test
  def testMergeModule() {
    val mod = new ViewMod(Map[String, AnyRef](
      "name" -> "test",
      "version" -> "test",
      "org" -> "test",
      "config_class" -> "test",
      "provider_class" -> "test",
      "file_extension" -> "test",
      "remote_location" -> "test",
      "local_location" -> "test",
      "repositories" -> List(Map("url"->"http://someurl.com","id"->"repo")),
      "dependencies" -> List(),
      "web_xml" -> List(Map("display-name"->"brzy servlet"))
      ))
    assertEquals(1,mod.repositories.size)
    mod.repositories.foreach(d => assertEquals("repo",d.id.get))

    val scalate = new ScalateModConfig(Map[String, AnyRef](
      "name" -> "test2",
      "version" -> "test2",
      "org" -> "test2",
      "config_class" -> "test2",
      "provider_class" -> "test2",
      "file_extension" -> "test2",
      "remote_location" -> "test2",
      "local_location" -> "test2",
      "repositories" -> null,
      "dependencies" -> List(),
      "web_xml" -> List(Map("description"->"test deesc"))
      ))
    val merged = {scalate << mod}.asInstanceOf[ScalateModConfig]
    assertNotNull(merged)
    assertEquals(1,merged.repositories.size)
    merged.repositories.foreach(d => assertEquals("repo",d.id.get))

    assertTrue(merged.fileExtension.isDefined)
    assertEquals("test",merged.fileExtension.get)

    assertEquals(2,merged.webXml.size)
  }

  @Test
  def testInstanceOf() {
    val plugin = new Mod(Map[String, AnyRef](
      "name" -> "test",
      "version" -> "test",
      "org" -> "test",
      "config_class" -> "test",
      "provider_class" -> "test",
      "file_extension" -> "test",
      "remote_location" -> "test"
      ))

    assertFalse(plugin.isInstanceOf[ScalateModConfig])
  }
}