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
import org.brzy.fab.task.Task
import tools.nsc.reporters.ConsoleReporter
import tools.nsc.doc.{Settings, DocFactory}
import org.brzy.fab.file.File

/**
 * Generates Javadoc and Scaladoc
 * 
 * @author Michael Fortin
 */
@Phase(name="doc",desc="Generate Documentation",defaultTask="doc-task")
class DocPhase(ctx:BuildContext) {

  @Task(name="pre-doc",desc="Document preperation")
  def preDocument = {
    ctx.line.say(Debug("pre-doc"))
  }

  @Task(name="doc-task",desc="Generates scaladoc and javadoc", dependsOn=Array("pre-doc"))
  def doDocument = {
    ctx.line.say(Debug("doc-task"))
    val docSettings = new Settings(error)
    val reporter = new ConsoleReporter(docSettings)
    val docProcessor = new DocFactory(reporter, docSettings)
    docProcessor.document(List(File(ctx.sourceDir,"scala").getAbsolutePath))
  }

  def error(str:String) = {
    ctx.line.endWithError(str)
  }
  
  override def toString = "Document Phase"
}