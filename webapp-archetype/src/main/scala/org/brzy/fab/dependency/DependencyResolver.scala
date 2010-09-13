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


import org.brzy.config.webapp.WebAppConfig
import org.brzy.fab.file.File
import org.apache.ivy.Ivy
import org.apache.ivy.core.module.id.ModuleRevisionId
import org.apache.ivy.core.retrieve.RetrieveOptions
import org.apache.ivy.ant.IvyReport
import org.apache.ivy.core.IvyContext
import org.apache.ivy.Ivy.IvyCallback
import org.brzy.fab.print._
import org.apache.ivy.util.MessageLoggerEngine

/**
 * This uses Ivy to download the application dependencies place them in a local cache director
 * so the compiler and packager can access them. 
 * 
 * @author Michael Fortin
 */
object DependencyResolver {

  val retrievePattern = ".brzy/app/lib/[conf]/[artifact]-[revision](-[classifier]).[type]"

  def apply(webappConfig:WebAppConfig)(implicit line:Conversation) {
    val base = File(".brzy/app")
    val settingsFile = File(".brzy/app/ivysettings.xml")
    val ivyFile = File(".brzy/app/ivy.xml")

    if(!base.exists)
       base.mkdirs

    val settingsXml = new IvySettingsXml(webappConfig)
    settingsXml.saveToFile(settingsFile.getAbsolutePath)

    val ivyXml = new IvyXml(webappConfig)
    ivyXml.saveToFile(ivyFile.getAbsolutePath)

    val ivy = new Ivy() {
      val logEngine = new MessageLoggerEngine {
        override def error(p1: String) = line.endWithError(p1)
        override def warn(p1: String) = line.say(Warn(p1))
        override def rawinfo(p1: String) = line.say(Debug(p1))
        override def info(p1: String) = line.say(Info(p1))
        override def deprecated(p1: String) = line.say(Warn(p1))
        override def verbose(p1: String) = line.say(Debug(p1))
        override def debug(p1: String) = line.say(Debug(p1))
      }
      override def getLoggerEngine = logEngine
    }
    ivy.bind
    ivy.execute(new IvyCallback() {
      def doInIvyContext(ivy: Ivy, context: IvyContext): java.lang.Object = {
        ivy.configure(settingsFile)
        ivy.getResolveEngine.resolve(ivyFile)
        val org = webappConfig.application.org.get
        val name = webappConfig.application.artifactId.get
        val version = webappConfig.application.version.get
        val modId = ModuleRevisionId.newInstance(org, name, version)
        ivy.getRetrieveEngine.retrieve(modId, retrievePattern, new RetrieveOptions)
        return null
      }
    })    
  }

  def report = {
    val ivyFile = File(".brzy/app/ivy.xml")
//    val report = new IvyReport()
//    report.doExecute()
  }
}