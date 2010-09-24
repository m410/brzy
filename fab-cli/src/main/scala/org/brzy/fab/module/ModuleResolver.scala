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


import org.apache.ivy.Ivy
import org.apache.ivy.core.retrieve.RetrieveOptions
import org.apache.ivy.core.IvyContext
import org.apache.ivy.core.module.id.ModuleRevisionId

import org.brzy.fab.file.{Files, File}
import org.brzy.fab.file.FileUtils._

import org.brzy.fab.dependency.Ivy._

import org.brzy.fab.print._
import org.brzy.fab.mod.ModConf


/**
 * This is run early in the Fab build.  It checks the configuration for modules and
 * downloads them using Ivy to a the ivy cache and a local application cache.
 *
 * @author Michael Fortin
 */
object ModuleResolver {
  val base = File(".brzy/modules")
  val settings = File(".brzy/modules/ivysettings.xml")
  val config = File(".brzy/modules/ivy.xml")
  val retrievePattern = ".brzy/modules/[artifact].[type]"

  def apply(initConfig: ModConf)(implicit line: Conversation): Unit = {
    line.say(Debug("modules: " + initConfig.modules))
    line.say(Debug("persistence: " + initConfig.persistence))
    line.say(Debug("views: " + initConfig.views))

    if (!base.exists)
      base.mkdirs

    val settingsXml = new IvySettingsXml()
    settingsXml.saveToFile(settings.getAbsolutePath)

    val ivyXml = new IvyXml(initConfig)
    ivyXml.saveToFile(config.getAbsolutePath)

    doInIvyCallback((ivy: Ivy, context: IvyContext) => {
      ivy.configure(settings)
      ivy.getResolveEngine.resolve(config)
      val modId = ModuleRevisionId.newInstance("org.brzy.fab", "modules", "1.0")
      val options = new RetrieveOptions

      ivy.getRetrieveEngine.retrieve(modId, retrievePattern, options)
      null
    })

    val modJars = Files(".brzy/modules/*.jar")
    modJars.foreach(f => {File(".brzy/modules/" + f.getName.substring(0, f.getName.length - 4)).mkdirs})
    modJars.foreach(f => {f.copyTo(File(".brzy/modules/" + f.getName.substring(0, f.getName.length - 4)))})
    Files(".brzy/modules/*/*.jar").foreach(zipfile => {
      zipfile.unzip
      zipfile.trash
    })

    val modImplJars = Files(".brzy/modules/*/*.jar")
  }
}