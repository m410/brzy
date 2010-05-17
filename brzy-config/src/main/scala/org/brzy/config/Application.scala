package org.brzy.config

import collection.JavaConversions._
import java.util.{Map => JMap}

/**
 *
 * @author Michael Fortin
 * @version $Id : $
 */
class Application(m: Map[String, AnyRef]) extends Config(m) {

  val version = set[String](m.get("version"))
  val name = set[String](m.get("name"))
  val author = set[String](m.get("author"))
  val description = set[String](m.get("description"))
  val groupId = set[String](m.get("group_id"))
  val artifactId = set[String](m.get("artifact_id"))

  //  val properties:Map[String,String] = m.get("artifact_id") match {
  //    case s:Some[JMap[_,_]] => {
  //      val jmap: JMap[_, _] = s.get
  //      jmap.toMap
  //    }
  //    case _ => null
  //  }

  val applicationClass: String = set[String](m.get("application_class"))
  val webappContext: String = set[String](m.get("webapp_context"))

  val configurationName = "Application Configuration"

  def asMap = {
    val map = new collection.mutable.HashMap[String, AnyRef]()
    map.put("name", name)
    map.put("version", version)
    map.put("group_id", groupId)
    map.put("artifact_id", artifactId)
    map.put("author", author)
    map.put("description", description)
    map.put("webapp_context", webappContext)
    map.put("application_class", applicationClass)
    Map[String,AnyRef]() ++ map
  }
}