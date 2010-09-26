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


import org.brzy.fab.print.Debug
import org.brzy.fab.build.BuildContext
import org.brzy.fab.task.Task

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
@Phase(name="publish",desc="Publish artifacts to remote repository",defaultTask="publish-task",dependsOn=Array("package"))
class PublishPhase(ctx:BuildContext) {

  @Task(name="publish-task",desc="Publishes the generated artifacts")
  def publish = {
    ctx.line.say(Debug("publish-task"))
    // publish library to remote maven or ivy repository
  }


  override def toString = "Publishing Phase"
}