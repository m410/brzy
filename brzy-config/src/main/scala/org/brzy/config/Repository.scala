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
  val snapshots: Option[Boolean] = m.get("snapshots").asInstanceOf[Option[Boolean]].orElse(Option(false))
  val releases: Option[Boolean] = m.get("releases").asInstanceOf[Option[Boolean]].orElse(Option(true))

  def asMap = {
    Map[String, AnyRef](
      "id" -> id,
      "name" -> name,
      "url" -> url,
      "snapshots" -> snapshots,
      "releases" -> releases,
      "snapshots" -> snapshots,
      "releases" -> releases)
  }
}