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

import collection.JavaConversions._
import org.brzy.config.common.{BootConfig, Config}

/**
 * Prints the configuration to the command line.  This is not up to date.
 * 
 * @author Michael Fortin
 */
class ConfigPrinter(config: Config) {
  println(config.configurationName)
  printMap("",config)

  protected def printMap(tab:String, c:Config):Unit = {
    val map = c.asMap
    map.foreach(map => {
      if(map._2.isInstanceOf[Config])
        printMap(tab + "  ", map._2.asInstanceOf[Config])
      else
        println(tab + map._1 + ": " + map._2)
    })
  }
}

object ConfigPrinter {
  def apply(config: BootConfig) = new ConfigPrinter(config)
}