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
package org.brzy.security

import org.brzy.fab.conf.BaseConf
import org.brzy.fab.mod.ViewMod

/**
 * Defines the Security configuration.
 *
 * @author Michael Fortin
 */
class SecurityModConfig(override val map: Map[String, AnyRef]) extends ViewMod(map) {
  val authorityEntity: Option[String] = map.get("authority_entity").asInstanceOf[Option[String]].orElse(None)
  val userEntity: Option[String] = map.get("user_entity").asInstanceOf[Option[String]].orElse(None)
  val userEntityName: Option[String] = map.get("user_entity_name").asInstanceOf[Option[String]].orElse(None)
  val userEntityPass: Option[String] = map.get("user_entity_pass").asInstanceOf[Option[String]].orElse(None)
  val algorithm: Option[String] = map.get("algorithm").asInstanceOf[Option[String]].orElse(None)
  val defaultPath: Option[String] = map.get("default_path").asInstanceOf[Option[String]].orElse(None)
  val authorityField: Option[String] = map.get("authority_field").asInstanceOf[Option[String]].orElse(None)

  override val webXml: Option[List[Map[String, AnyRef]]] = map.get("web_xml").asInstanceOf[Option[List[Map[String, AnyRef]]]].orElse(None)

  override def <<(that: BaseConf) = {
    if (that == null) {
      this
    }
    else {
      new SecurityModConfig(Map[String, AnyRef](
        "authority_entity" -> that.map.getOrElse("authority_entity", this.authorityEntity.orNull),
        "user_entity" -> that.map.getOrElse("user_entity", this.userEntity.orNull),
        "user_entity_name" -> that.map.getOrElse("user_entity_name", this.userEntityName.orNull),
        "user_entity_pass" -> that.map.getOrElse("user_entity_pass", this.userEntityPass.orNull),
        "algorithm" -> that.map.getOrElse("algorithm", this.algorithm.orNull),
        "default_path" -> that.map.getOrElse("default_path", this.defaultPath.orNull),
        "authority_field" -> that.map.getOrElse("authority_field", this.authorityField.orNull),
        "web_xml" -> {
          if (this.webXml.isDefined && this.webXml.get != null &&
                  that.map.get("web_xml").isDefined && that.map.get("web_xml").get != null)
            this.webXml.get ++ that.map.get("web_xml").get.asInstanceOf[List[_]]
          else if (this.webXml.isDefined)
            this.webXml.get.asInstanceOf[List[_]]
          else if (that.map.get("web_xml").isDefined)
            that.map.get("web_xml").get
          else
            null
        }) ++ super.<<(that).map)
    }
  }
}