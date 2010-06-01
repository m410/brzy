package org.brzy.config


/**
 *
 * @author Michael Fortin
 * @version $Id : $
 */
class Application(m: Map[String, AnyRef]) extends Config(m) {
  val configurationName: String = "Application Configuration"
  val version: Option[String] = m.get("version").asInstanceOf[Option[String]].orElse(None)
  val name: Option[String] = m.get("name").asInstanceOf[Option[String]].orElse(None)
  val author: Option[String] = m.get("author").asInstanceOf[Option[String]].orElse(None)
  val description: Option[String] = m.get("description").asInstanceOf[Option[String]].orElse(None)
  val org: Option[String] = m.get("org").asInstanceOf[Option[String]].orElse(None)
  val artifactId: Option[String] = m.get("artifact_id").asInstanceOf[Option[String]].orElse(None)
  val applicationClass: Option[String] = m.get("application_class").asInstanceOf[Option[String]].orElse(None)
  val webappContext: Option[String] = m.get("webapp_context").asInstanceOf[Option[String]].orElse(None)

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
      "name" -> name.getOrElse(null),
      "version" -> version.getOrElse(null),
      "org" -> org.getOrElse(null),
      "artifact_id" -> artifactId.getOrElse(null),
      "author" -> author.getOrElse(null),
      "description" -> description.getOrElse(null),
      "webapp_context" -> webappContext.getOrElse(null),
      "application_class" -> applicationClass.getOrElse(null))
  }
}