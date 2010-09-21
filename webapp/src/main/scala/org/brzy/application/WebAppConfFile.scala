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
package org.brzy.application

import org.brzy.fab.conf._

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
class WebAppConfFile(override val map: Map[String, AnyRef]) extends ModConf(map) {
  
	val environment: Option[String] = map.get("environment").asInstanceOf[Option[String]].orElse(None)
  
	val application: Option[Application] = map.get("application") match {
    case s: Some[_] => Option(new Application(s.get.asInstanceOf[Map[String, String]]))
    case _ => None
  }
  
  val project: Option[Project] = map.get("project") match {
    case Some(s) =>
      if (s != null)
        Option(new Project(s.asInstanceOf[Map[String, String]]))
      else
        None
    case _ => None
  }

  val logging: Option[Logging] = map.get("logging") match {
    case Some(s) =>
      if (s != null)
        Option(new Logging(s.asInstanceOf[Map[String, AnyRef]]))
      else
        None
    case _ => None
  }

  val webXml: Option[List[Map[String, AnyRef]]] = map.get("web_xml") match {
    case Some(s) =>
      if (s != null && s.isInstanceOf[List[_]])
        Option(s.asInstanceOf[List[Map[String, AnyRef]]])
      else
        None
    case _ => None
  }
}