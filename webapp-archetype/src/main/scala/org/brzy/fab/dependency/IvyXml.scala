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


import xml._
import org.brzy.config.webapp.WebAppConfig
import java.io.{BufferedWriter, FileWriter}
import java.io.File

/**
 * http://draconianoverlord.com/2010/07/18/publishing-to-maven-repos-with-ivy.html
 * 
 * @author Michael Fortin
 */
class IvyXml(config:WebAppConfig) {

  // work around for bug with sorted sets and xml
  private val dependencies = config.dependencies.toList

  val ivy =
<ivy-module version="2.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:noNamespaceSchemaLocation="http://ant.apache.org/ivy/schemas/ivy.xsd">
  <info module={config.application.artifactId.get} organisation={config.application.org.get} revision={config.application.version.get} />
  <configurations>
    <conf name="default" />
    <conf name="provided" extends="default"/>
    <conf name="compile" extends="provided"/>
    <conf name="test" extends="default" />
  </configurations>
  <publications>
    <artifact type="pom" ext="pom" conf="default"/>
    <artifact type="jar" ext="jar" conf="default"/>
  </publications>
  <dependencies defaultconfmapping="*->*,!sources,!javadoc" defaultconf="default">
    {for(dp <- dependencies) yield
    if(dp.excludes.isDefined)
    <dependency org={dp.org.get} name={dp.name.get} rev={dp.rev.get} conf={dp.conf.get} transitive={dp.transitive.getOrElse(true).toString}>
      {for(exclude <- dp.excludes.get) yield
      <exclude org={exclude.org.get} name={exclude.name.get} />
      }
    </dependency>
    else
    <dependency org={dp.org.get} name={dp.name.get} rev={dp.rev.get} conf={dp.conf.get} transitive={dp.transitive.getOrElse(true).toString}/>
    }
    {for(exclude <- config.dependencyExcludes) yield {
    <exclude org={exclude.org.get} module={exclude.name.get} />
    }}
  </dependencies>
</ivy-module>

  def saveToFile(path:String) = XML.save(path, ivy, "UTF-8", true, null)
}