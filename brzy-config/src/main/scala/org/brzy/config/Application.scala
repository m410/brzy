package org.brzy.config


/**
 *
 * @author Michael Fortin
 * @version $Id : $
 */
class Application(m: Map[String, AnyRef]) extends Config(m) {
  val configurationName: String = "Application Configuration"
  val version: Option[String] = m.get("version").asInstanceOf[Option[String]].orElse(Option(null))
  val name: Option[String] = m.get("name").asInstanceOf[Option[String]].orElse(Option(null))
  val author: Option[String] = m.get("author").asInstanceOf[Option[String]].orElse(Option(null))
  val description: Option[String] = m.get("description").asInstanceOf[Option[String]].orElse(Option(null))
  val org: Option[String] = m.get("org").asInstanceOf[Option[String]].orElse(Option(null))
  val artifactId: Option[String] = m.get("artifact_id").asInstanceOf[Option[String]].orElse(Option(null))
  val applicationClass: Option[String] = m.get("application_class").asInstanceOf[Option[String]].orElse(Option(null))
  val webappContext: Option[String] = m.get("webapp_context").asInstanceOf[Option[String]].orElse(Option(null))

  // TODO later
  //  val properties:Map[String,String] = m.get("artifact_id") match {
  //    case s:Some[JMap[_,_]] => {
  //      val jmap: JMap[_, _] = s.get
  //      jmap.toMap
  //    }
  //    case _ => null
  //  }

  def asMap = {
    Map[String, AnyRef](
      "name" -> name,
      "version" -> version,
      "org" -> org,
      "artifact_id" -> artifactId,
      "author" -> author,
      "description" -> description,
      "webapp_context" -> webappContext,
      "application_class" -> applicationClass)
  }
}