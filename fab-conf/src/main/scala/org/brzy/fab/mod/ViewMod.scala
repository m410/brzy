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
package org.brzy.fab.mod

import org.brzy.fab.conf.{WebXml, BaseConf}

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
class ViewMod(override val map: Map[String, AnyRef]) extends RuntimeMod(map) with WebXml {

  val fileExtension: Option[String] = map.get("file_extension").asInstanceOf[Option[String]].orElse(None)

  val webXml: Option[List[Map[String, AnyRef]]] = map.get("web_xml").asInstanceOf[Option[List[Map[String, AnyRef]]]].orElse(None)

  override def <<(it: BaseConf) = {
    if (it == null)
      this
    else {
      new ViewMod(Map[String, AnyRef](
        "file_extension" -> it.map.getOrElse("file_extension",this.fileExtension.orNull),
        "web_xml" -> {
          if (this.webXml.isDefined && this.webXml.get != null &&
                  it.map.get("web_xml").isDefined && it.map.get("web_xml").get != null)
            this.webXml.get ++ it.map.get("web_xml").get.asInstanceOf[List[_]]
          else if (this.webXml.isDefined)
            this.webXml.get.asInstanceOf[List[_]]
          else if (it.map.get("web_xml").isDefined)
            it.map.get("web_xml").get
          else
            null
        }) ++ super.<<(it).map)
    }
  }
}