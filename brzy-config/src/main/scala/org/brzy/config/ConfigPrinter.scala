package org.brzy.config

import collection.JavaConversions._

/**
 *
 * @author Michael Fortin
 * @version $Id: $
 */
class ConfigPrinter(config:AppConfig) {

  println("Application")
  printReflectObj("  ",config.application)
  println("")
  println("Project")
  printReflectObj("  ",config.project)
  println("")
  println("Repositories")
  printArray(config.repositories)
  println("")
  println("Dependencies")
  printArray(config.dependencies)
  println("")
  println("Logging")
  printReflectObj("  ",config.logging)
  println("")
  println("Web.xml")
  printArray(config.web_xml.toArray)
  println("")
  println("Plugins")
  printArray(config.plugins)
  println("")

  def printArray(list:Array[_<:AnyRef]) = {
      list.foreach(printReflectObj("  ",_))
  }

  def printReflectObj(tab:String, obj:AnyRef) = {
    if(obj.isInstanceOf[java.util.Map[_,_]]) {
      obj.asInstanceOf[java.util.Map[_,_]].foreach(x => println(tab + x._1 + ": " + x._2))
    }
    else {
      val clazz = obj.getClass
      clazz.getMethods.foreach(f =>
        if (!f.getName.startsWith("get") &&
            !f.getName.startsWith("set") &&
            !f.getName.startsWith("$") &&
            !f.getName.endsWith("_$eq") &&
            f.getName.compareTo("wait") != 0 &&
            f.getName.compareTo("hashCode") != 0 &&
            f.getName.compareTo("equals") != 0 &&
            f.getName.compareTo("notify") != 0 &&
            f.getName.compareTo("toString") != 0 &&
            f.getName.compareTo("notifyAll") != 0 &&
            f.getParameterTypes.length == 0) {
          println(tab + f.getName + ": " + f.invoke(obj))
        })
    }
  }
}

object ConfigPrinter {
  def apply(config:AppConfig) = new ConfigPrinter(config)
}