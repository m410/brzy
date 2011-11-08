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
package org.brzy.mod.jpa

import org.brzy.fab.mod.PersistenceMod
import org.brzy.fab.conf.{WebXml, BaseConf}
import java.io.PrintWriter

/**
 * JPA module configuration parameters.
 *
 * @author Michael Fortin
 */
class JpaModConfig(override val map: Map[String, AnyRef]) extends PersistenceMod(map) {
  val persistenceUnit: Option[String] = map.get("persistence_unit").asInstanceOf[Option[String]].orElse(None)
  val transactionType: Option[String] = map.get("transaction_type").asInstanceOf[Option[String]].orElse(None)
  val entityDiscovery: String = map.getOrElse("entity_discovery","list").asInstanceOf[String] // or scan
  val entities: Option[List[String]] = map.get("entities").asInstanceOf[Option[List[String]]].orElse(None)
  val properties: Option[Map[String,String]] = map.get("properties").asInstanceOf[Option[Map[String,String]]].orElse(None)
  val webXml: Option[List[Map[String, AnyRef]]] = map.get("web_xml").asInstanceOf[Option[List[Map[String, AnyRef]]]].orElse(None)

  override def <<(that: BaseConf) =
    if (that == null)
      this
    else
      new JpaModConfig(Map[String, AnyRef](
        "persistence_unit" -> that.map.getOrElse("persistence_unit", this.persistenceUnit.orNull),
        "transaction_type" -> that.map.getOrElse("transaction_type", this.transactionType.orNull),
        "entity_discovery" -> that.map.getOrElse("entity_discovery", this.entityDiscovery),
        "entities" -> that.map.getOrElse("entities", this.entities.orNull),
        "properties" -> that.map.getOrElse("properties", this.properties.orNull)
      ) ++ super.<<(that).map)

  override def prettyPrint(t: String, pw: PrintWriter) {
    val tab = t + "  "
    super.prettyPrint(tab,pw)
    pw.print(tab +"presistence_unit: ")
    pw.println(persistenceUnit.getOrElse("<None>"))
    pw.print(tab +"transaction_type: ")
    pw.println(transactionType.getOrElse("<None>"))
    pw.print(tab +"entity_discovery: ")
    pw.println(entityDiscovery)
    pw.print(tab +"entities: ")
    pw.println(entities.getOrElse("<None>"))
    pw.print(tab +"properties: ")
    pw.println(properties.getOrElse("<None>"))
    pw.print(tab +"web_xml: ")
    pw.println(webXml.getOrElse("<None>"))
  }
}