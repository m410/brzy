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
package org.brzy.fab.module


import xml.XML

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
class IvySettingsXml {

  val xml =
<ivysettings>
  <property name="revision" value="SNAPSHOT" override="false"/>
  <settings defaultResolver="default"/>
  <resolvers>
    <ibiblio name="maven-local" m2compatible="true" root="file://${user.home}/.m2/repository"/>
    <ibiblio name="brzy-nexus" m2compatible="true" root="http://brzy.org/nexus/content/repositories/releases"/>
    <ibiblio name="brzy-nexus-snapshots" m2compatible="true" root="http://brzy.org/nexus/content/repositories/snapshots"/>
    <chain name="default">
      <resolver ref="maven-local"/>
      <resolver ref="brzy-nexus"/>
      <resolver ref="brzy-nexus-snapshots"/>
    </chain>
  </resolvers>
</ivysettings>

  def saveToFile(path:String) = XML.save(path, xml, "UTF-8", true, null)
}