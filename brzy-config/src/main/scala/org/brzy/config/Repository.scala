package org.brzy.config

/**
 * @author Michael Fortin
 * @version $Id : $
 */
class Repository(m: Map[String, AnyRef]) extends Config(m) {
  val configurationName: String = "Repository"
  val id: Option[String] = m.get("id").asInstanceOf[Option[String]].orElse(None)
  val name: Option[String] = m.get("name").asInstanceOf[Option[String]].orElse(None)
  val url: Option[String] = m.get("url").asInstanceOf[Option[String]].orElse(None)
  val snapshots: Option[Boolean] = m.get("snapshots").asInstanceOf[Option[Boolean]].orElse(Option(true))
  val releases: Option[Boolean] = m.get("releases").asInstanceOf[Option[Boolean]].orElse(Option(false))

  override def asMap:Map[String,AnyRef] = {
    Map[String, Any](
      "id" -> id.getOrElse(null),
      "name" -> name.getOrElse(null),
      "url" -> url.getOrElse(null),
      "snapshots" -> snapshots.getOrElse(null),
      "releases" -> releases.getOrElse(null)).asInstanceOf[Map[String,AnyRef]]
  }
}