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
package org.brzy.webapp.application

import org.brzy.fab.conf._
import java.io.PrintWriter
import org.brzy.fab.mod.ProjectModuleConfiguration

/**
 * This holds all the web-app.b.yml configuration elements before it's initialized.  It's very
 * similar to the WebAppConf and used to initialize it.
 *
 * @author Michael Fortin
 */
class WebAppConfFile(override val map: Map[String, AnyRef]) extends ProjectModuleConfiguration(map) {
 

  val useSsl: Boolean = map.get("use_ssl") match {
    case Some(e) => e.asInstanceOf[Boolean]
    case _ => false
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


  override def prettyPrint(t: String, pw: PrintWriter) {
    val tab = t + "  "
    super.prettyPrint(tab,pw)
    pw.println("Use SSL: " + useSsl)
    pw.println("Logging")
    logging match {
      case Some(l) => l.prettyPrint(tab,pw)
      case _ => pw.println("<None>")
    }

    pw.println("web.xml")
    webXml match {
      case Some(l) => pw.println(tab + l)
      case _ => pw.println("<None>")
    }
  }

  override def <<(it: BaseConf) = {
    if(it == null)
      this
    else {
      val that = it.asInstanceOf[WebAppConfFile]
      new WebAppConfFile(Map[String, AnyRef](
        "use_ssl" -> {{if (that.useSsl) that.useSsl else this.useSsl}.asInstanceOf[AnyRef]},
        "environment" -> {if( this.environment != "") this.environment else that.environment},
        "application" -> {this.application.getOrElse(that.application.get)}.map,
        "build" -> {
          if (this.build.isDefined && this.build.get != null)
            {this.build.get << that.build.orNull}.map
          else if (that.build.isDefined && that.build.get != null)
            that.build.get.map
          else
            null
        },
        "logging" -> {
          if (this.logging.isDefined)
            {this.logging.get << that.logging.orNull}.map
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