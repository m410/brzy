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
  
	val environment: Option[String] = map.get("environment") match {
    case Some(e) => if(e != null) Option(e.asInstanceOf[String]) else None
    case _ => None
  }
  
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


  override def <<(it: BaseConf) = {
    if(it == null)
      this
    else {
      val that = it.asInstanceOf[WebAppConfFile]
      new WebAppConfFile(Map[String, AnyRef](
        "environment" -> this.environment.getOrElse(that.environment.getOrElse(null)),
        "application" -> {this.application.getOrElse(that.application.get)}.map,
        "project" -> {
          if (this.project.isDefined && this.project.get != null)
            {this.project.get << that.project.getOrElse(null)}.map
          else if (that.project.isDefined && that.project.get != null)
            that.project.get.map
          else
            null
        },
        "logging" -> {
          if (this.logging.isDefined)
            {this.logging.get << that.logging.getOrElse(null)}.map
          else if (that.logging.isDefined)
            that.logging.get.map
          else
            None
        },
        "web_xml" -> {
          if (this.webXml.isDefined && that.webXml.isDefined)
            this.webXml.get ++ that.webXml.get
          else if (this.webXml.isDefined)
            this.webXml.get
          else if (that.webXml.isDefined)
            that.webXml.get
          else
            None
        }) ++ super.<<(that).map)
    }
  }
}