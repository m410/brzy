package org.brzy.config.common


/**
 *
 * @author Michael Fortin
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

  def asMap = m
}