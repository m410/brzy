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
package org.brzy.jpa.build

import org.brzy.webapp.ConfigFactory
import xml.XML
import java.io.File

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */

object PersistenceXmlMain {
  def main(args: Array[String]) = {
    println("[0]config = " + args(0))
    println("[1]env = " + args(1))
    println("[2]destination = " + args(2))
    val bootConfig = ConfigFactory.makeBootConfig(new File(args(0)), args(1))
    val parent = new File(args(2))
    val file = new File(parent,"logback.xml")
    XML.save(file.getAbsolutePath, new PersistenceXml(bootConfig).body)
  }
}