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

  val excludes = m.get("excludes") match {
    case s:Some[JList[_]] => s.get.toList
    case _ => null
  }

  val configurationName = "Dependency"

  def asMap = {
    val map = collection.mutable.HashMap[String,AnyRef]()
    map.put("org", org)
    map.put("name", name)
    map.put("rev", rev)
    map.put("conf", conf)

    if(excludes != null)
      map.put("excludes", excludes.map(f=>f.asInstanceOf[Dependency].asMap))

    Map[String,AnyRef]() ++ map
  }
}