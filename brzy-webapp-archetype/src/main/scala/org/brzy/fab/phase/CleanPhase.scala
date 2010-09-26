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


import org.brzy.fab.task.Task
import org.brzy.fab.build.BuildContext
import org.brzy.fab.print.Debug
import org.brzy.fab.file.FileUtils._
import java.io.File

/**
 * @author Michael Fortin
 */
@Phase(name="clean",desc="Clean Generated Artifacts",defaultTask="clean-task")
class CleanPhase(ctx:BuildContext) {

  @Task(name="pre-clean",desc="Pre-clean")
  def preClean = {
    ctx.line.say(Debug("pre-clean"))
  }

  @Task(name="clean-task",desc="Deletes build artifacts", dependsOn=Array("pre-clean"))
  def clean = {
    ctx.line.say(Debug("clean-task"))
    ctx.targetDir.trash
    val webInf = new File(ctx.webappDir,"WEB-INF")
    webInf.trash
  }

  override def toString = "Clean Phase"
}