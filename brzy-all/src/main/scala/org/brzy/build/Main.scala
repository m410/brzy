package org.brzy.build

import org.brzy.config.Builder
import xml.XML

/**
 *
 * @author Michael Fortin
 * @version $Id: $
 */
object Main {

  def main(args: Array[String]) {
    println("config = " + args(2))
    println("target = " + args(3))
    val config = new Builder(args(2), args(0)).config

    args(1) match {
      case "logback" =>
        XML.save(args(3), new LogBackXml(config).body)

      case "web" =>
        XML.save(args(3), new WebXml(config).body)

      case "persistence" =>
        XML.save(args(3), new PersistenceXml(config).body)
    }
  }
}