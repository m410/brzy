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
package org.brzy.fab.cli.module


import xml._
import org.brzy.fab.cli.mod.ModConf


/**
 * http://draconianoverlord.com/2010/07/18/publishing-to-maven-repos-with-ivy.html
 *
 * @author Michael Fortin
 */
class IvyXml(config:ModConf) {

  val xml =
<ivy-module version="2.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:noNamespaceSchemaLocation= "http://ant.apache.org/ivy/schemas/ivy.xsd"
    xmlns:m="http://ant.apache.org/ivy/maven">
  <info module="modules" organisation="org.brzy.fab" revision="1.0"/>
  <configurations>
    <conf name="default" />
  </configurations>
  <dependencies defaultconfmapping="*->*,!sources,!javadoc" defaultconf="default">
  {if(config.modules.isDefined)
      for(mod <-config.modules.get) yield
      <dependency org={mod.org.get} name={mod.name.get} rev={mod.version.get} m:classifier="module" transitive="false" />
  }
  {if(config.persistence.isDefined)
    for(psst <- config.persistence.get) yield
    <dependency org={psst.org.get} name={psst.name.get} rev={psst.version.get} m:classifier="module" transitive="false" />
  }
  {if(config.views.isDefined){
    val view = config.views.get
    <dependency org={view.org.get} name={view.name.get} rev={view.version.get} m:classifier="module" transitive="false" />
  }}
  </dependencies>
</ivy-module>

  def saveToFile(path:String) = XML.save(path, xml, "UTF-8", true, null)
}