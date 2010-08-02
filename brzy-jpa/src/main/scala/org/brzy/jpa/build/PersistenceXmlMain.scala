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