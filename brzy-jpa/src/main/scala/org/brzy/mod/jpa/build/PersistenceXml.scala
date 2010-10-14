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

/**
 * Generates a Persistence.xml JPA configuration file from the applications configuration.
 * 
 * @author Michael Fortin
 */
class PersistenceXml(config: WebAppConf) {

  val entityClasses = List.empty[Class[_]]
  val properties = Map.empty[String,String]
  val transactionType = "RESOURCE_LOCAL"
  
  val xml =
<persistence xmlns="http://java.sun.com/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://java.sun.com/xml/ns/persistence
        http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd" version="2.0">
  <persistence-unit name="brzy-webapp-persistence" transaction-type={transactionType}>
    <provider>org.hibernate.ejb.HibernatePersistence</provider>
    <!-- classes -->
    {entityClasses.map(cls=>{
    <class>{cls.getName}</class>
    })}
    <properties>
      {properties.map(nvp=>{
      <property name={nvp._1} value={nvp._2} />
      })}
      <!--
      <property name="hibernate.hbm2ddl.auto" value="create"/>
      <property name="hibernate.archive.autodetection" value="class, hbm"/>
      <property name="hibernate.show_sql" value="true"/>
      <property name="hibernate.connection.driver_class" value="com.mysql.jdbc.Driver"/>
      <property name="hibernate.connection.password" value="<PASSWORD>"/>
      <property name="hibernate.connection.url" value="jdbc:mysql://<HOST IP ADDRESS>/<DB NAME>"/>
      <property name="hibernate.connection.username" value="<USERNAME>"/>
      <property name="hibernate.dialect" value="org.hibernate.dialect.MySQLDialect"/>
      <property name="hibernate.c3p0.min_size" value="5"/>
      <property name="hibernate.c3p0.max_size" value="20"/>
      <property name="hibernate.c3p0.timeout" value="300"/>
      <property name="hibernate.c3p0.max_statements" value="50"/>
      <property name="hibernate.c3p0.idle_test_period" value="3000"/>
      -->
    </properties>
  </persistence-unit>
</persistence>

  def saveToFile(path:String) = XML.save(path, xml, "UTF-8", true, null)
}
