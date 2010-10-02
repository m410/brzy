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
package org.brzy.fab.phase


import org.brzy.fab.build.BuildContext
import org.brzy.fab.print.Debug
import tools.nsc.reporters.ConsoleReporter
import tools.nsc.doc.{Settings, DocFactory}
import org.brzy.fab.dependency.DependencyResolver
import org.brzy.fab.file.{Files, File}
import collection.immutable.List
import java.io.{File => JFile}
import org.brzy.application.WebAppConf

/**
 * Generates Javadoc and Scaladoc
 *
 * @author Michael Fortin
 */
class DocPhase(ctx: BuildContext) {
  def preDocument = {
    ctx.line.say(Debug("pre-doc"))
    File(ctx.targetDir, "dependency-report").mkdirs
    File(ctx.targetDir, "scaladoc").mkdirs
  }

  /**
   * http://lampsvn.epfl.ch/trac/scala/wiki/Scaladoc
   */
  def doDocument = {
    ctx.line.say(Debug("doc-task"))
    File("target/scala-doc").mkdirs
    val docSettings = new Settings((str: String) => { ctx.line.endWithError(str)})
    docSettings.d.value = File("target/scala-doc").getAbsolutePath
    docSettings.classpath.value = ""
    val cp = Files(".brzy/app/compile/*.jar")

    cp.foreach(path => {
      val pathStr = path.getAbsolutePath
      docSettings.classpath.append(pathStr)
    })
     docSettings.classpath.append(File(ctx.targetDir,"classes").getAbsolutePath)

    val reporter = new ConsoleReporter(docSettings)
    val docProcessor = new DocFactory(reporter, docSettings)
    val srcDir = File(ctx.sourceDir, "scala")
    val srcFiles: List[JFile] = sourceFiles(srcDir)
    docProcessor.document(srcFiles.map(_.getAbsolutePath))
  }

  def dependecyReport = {
    ctx.line.say(Debug("doc-task"))
    DependencyResolver.generateReport(ctx.properties("webAppConfig").asInstanceOf[WebAppConf])
  }

  private def sourceFiles(root: JFile): List[JFile] = {
    if (root.isFile && root.getName.endsWith(".java") || root.getName.endsWith(".scala"))
      List(root)
    else
      makeList(root.listFiles).flatMap {f => sourceFiles(f)}
  }

  private def makeList( a: Array[JFile] ): List[JFile] = {
    if( a == null )
      Nil
    else
      a.toList
  }

  override def toString = "Document Phase"
}