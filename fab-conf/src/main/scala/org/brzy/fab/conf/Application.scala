/*
 * Copyright 2010 Michael Fortin <mike@brzy.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.brzy.fab.conf

/**
 *
 * @author Michael Fortin
 */
class Application(val map: Map[String, AnyRef])  {

  val version: Option[String] = map.get("version").asInstanceOf[Option[String]].orElse(None)
  val name: Option[String] = map.get("name").asInstanceOf[Option[String]].orElse(None)
  val description: Option[String] = map.get("description").asInstanceOf[Option[String]].orElse(None)
  val author: Option[String] = map.get("author").asInstanceOf[Option[String]].orElse(None)
  val org: Option[String] = map.get("org").asInstanceOf[Option[String]].orElse(None)
  val artifactId: Option[String] = map.get("artifact_id").asInstanceOf[Option[String]].orElse(None)

  val applicationClass: Option[String] = map.get("application_class").asInstanceOf[Option[String]].orElse(None)

//  val webappContext: Option[String] = map.get("webapp_context").asInstanceOf[Option[String]].orElse(None)

  // TODO later
  //  val properties:Map[String,String] = m.get("artifact_id") match {
  //    case s:Some[JMap[_,_]] => {
  //      val jmap: JMap[_, _] = s.get
  //      jmap.toMap
  //    }
  //    case _ => null
  //  }
}