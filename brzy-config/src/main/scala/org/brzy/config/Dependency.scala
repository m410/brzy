package org.brzy.config

import java.util.{List=>JList}
import collection.JavaConversions._

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class Dependency(m:Map[String,AnyRef]) extends Config(m) {

  val org =  set[String](m.get("org"))
  val name = set[String](m.get("name"))
  val rev = set[String](m.get("rev"))
  val conf = set[String](m.get("conf"))

  val exculdes = m.get("excludes") match {
    case s:Some[JList[_]] => s.get.toList
    case _ => null
  }

  val configurationName = "Dependency"

  def asMap = {
    val map = Map[String,AnyRef]()
    // TODO add each property
    map
  }
}