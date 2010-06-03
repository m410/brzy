package org.brzy.config

import collection.JavaConversions._
import common.{BootConfig, Config}

/**
 *
 * @author Michael Fortin
 * @version $Id : $
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