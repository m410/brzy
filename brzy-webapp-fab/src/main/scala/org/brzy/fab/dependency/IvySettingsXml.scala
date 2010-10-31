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
package org.brzy.fab.dependency


import xml.XML
import org.brzy.application.WebAppConf

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
class IvySettingsXml(config:WebAppConf) {
  private val repos = config.repositories.toList
/* <filesystem name="libraries">
      <artifact pattern="${ivy.project.dir}/lib/[artifact]-[revision](-[classifier]).[ext]" />
    </filesystem>
      <resolver ref="libraries"/> */
  val xml =
<ivysettings>
  <property name="revision" value="SNAPSHOT" override="false"/>
  <settings defaultResolver="default"/>
  <caches defaultCacheDir="${ivy.basedir}/.brzy/ivy-cache" />
  <resolvers>
    <ibiblio name="maven-local" root="file://${user.home}/.m2/repository" m2compatible="true" />
    {for(repo <- repos; if(repo.id.isDefined && repo.url.isDefined)) yield
    <ibiblio m2compatible="true" name={repo.id.get} root={repo.url.get}  />
    }
    <chain name="default">
      {for(repo <- repos;if(repo.id.isDefined && repo.url.isDefined)) yield
      <resolver ref={repo.id.get} />
      }
      <resolver ref="maven-local"/>
    </chain>
  </resolvers>
</ivysettings>

  def saveToFile(path:String) = XML.save(path, xml, "UTF-8", true, null)
}