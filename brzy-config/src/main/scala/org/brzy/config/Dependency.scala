package org.brzy.config

import collection.mutable.ListBuffer

/**
 * @author Michael Fortin
 * @version $Id : $
 */
class Dependency(m: Map[String, AnyRef]) extends Config(m) {
  val configurationName: String = "Dependency"
  val org: Option[String] = m.get("org").asInstanceOf[Option[String]].orElse(None)
  val name: Option[String] = m.get("name").asInstanceOf[Option[String]].orElse(None)
  val rev: Option[String] = m.get("rev").asInstanceOf[Option[String]].orElse(None)
  val conf: Option[String] = m.get("conf").asInstanceOf[Option[String]].orElse(None)

  val excludes: Option[List[Dependency]] = m.get("excludes") match {
    case s: Some[List[Dependency]] =>
      val buffer = new ListBuffer[Dependency]()
      s.get.foreach(exclude => buffer += new Dependency(exclude.asInstanceOf[Map[String, String]]))
      Option(buffer.toList)
    case _ => None
  }

  def asMap = {
    Map[String, AnyRef](
      "org" -> org.getOrElse(null),
      "name" -> name.getOrElse(null),
      "rev" -> rev.getOrElse(null),
      "conf" -> conf.getOrElse(null),
      "exculdes" -> {excludes match {
        case s: Some[List[Dependency]] => s.get.map(_.asMap).toList
        case _ => null
      }})
  }

}