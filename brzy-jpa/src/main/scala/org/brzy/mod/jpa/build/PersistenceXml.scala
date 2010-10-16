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
package org.brzy.mod.jpa.build

import xml._
import org.brzy.application.WebAppConf
import org.brzy.mod.jpa.JpaModConfig

/**
 * Generates a Persistence.xml JPA configuration file from the applications configuration.
 * 
 * @author Michael Fortin
 */
class PersistenceXml(config: WebAppConf) {
  val jpaModConf = config.persistence.find(_.name.get == "brzy-jpa").get.asInstanceOf[JpaModConfig]

  val transactionType = jpaModConf.transactionType.getOrElse("RESOURCE_LOCAL")
  val persistenceUnitName = jpaModConf.persistenceUnit.getOrElse("brzy-unit")
  val properties = jpaModConf.properties.getOrElse(Map.empty[String,String])

  val entityClasses:List[String] = 
      if(jpaModConf.entityDiscovery == "list") {
        val list = jpaModConf.entities match {
          case Some(list) => list
          case _ => List.empty[String]
        }
        list
      }
      else { // scan for entities
        List.empty[String]
      }

  
  val xml =
<persistence xmlns="http://java.sun.com/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://java.sun.com/xml/ns/persistence
        http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd" version="2.0">
  <persistence-unit name={persistenceUnitName} transaction-type={transactionType}>
    <provider>org.hibernate.ejb.HibernatePersistence</provider>
    {entityClasses.map(c=>{<class>{c}</class>})}
    <properties>
      {properties.map(nvp=>{
      <property name={nvp._1} value={nvp._2} />
      })}
    </properties>
  </persistence-unit>
</persistence>

  def saveToFile(path:String) = XML.save(path, xml, "UTF-8", true, null)
}
