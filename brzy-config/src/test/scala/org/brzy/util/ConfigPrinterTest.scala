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
package org.brzy.util

import org.junit.Test
import org.junit.Assert._
import java.util.{Map => JMap}
import org.ho.yaml.Yaml
import collection.JavaConversions._
import org.brzy.util.NestedCollectionConverter._
import org.scalatest.junit.JUnitSuite
import org.brzy.config.common.BootConfig


class ConfigPrinterTest extends JUnitSuite {

  @Test
  def testPrint() = {
    val url = getClass.getClassLoader.getResource("brzy-webapp.b.yml")
    val config = Yaml.load(url.openStream)
    config.asInstanceOf[JMap[String, AnyRef]].put("environment", "development")
    val app = new BootConfig(convertMap(config.asInstanceOf[JMap[String, AnyRef]]))
    ConfigPrinter(app)
    assertTrue(true)
  }

  @Test
  def testPrintDefault() = {
    val url = getClass.getClassLoader.getResource("brzy-webapp.default.b.yml")
    val config = Yaml.load(url.openStream)
    config.asInstanceOf[JMap[String, AnyRef]].put("environment", "development")
    val app = new BootConfig(convertMap(config.asInstanceOf[JMap[String, AnyRef]]))
    ConfigPrinter(app)
    assertTrue(true)
  }
}