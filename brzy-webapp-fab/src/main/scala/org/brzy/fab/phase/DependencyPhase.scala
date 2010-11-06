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
import org.brzy.fab.dependency.DependencyResolver
import org.brzy.fab.file.File
import org.brzy.application.WebAppConf

/**
 * A build phase is a set of tasks, grouped together to accomplish a goal.  This is very
 * similar in concept to maven phases.  A phase can depend on other phases.  An when
 * running the phase, the dependencies are run first.  When running just a task in the phase
 * is called to run only it's dependencies task are run. The phase dependent phases are not. 
 * 
 * @author Michael Fortin
 */
class DependencyPhase(ctx:BuildContext) {

  def preResolve = {
    ctx.line.say(Debug("pre-resolve"))
    File(".brzy/app").mkdirs
  }

  def dependencies = {
    ctx.line.say(Debug("resolve-task"))
    implicit val conversation = ctx.line
    try {
      if(ctx.installLibs)
        DependencyResolver(ctx.properties("webAppConfig").asInstanceOf[WebAppConf])
    }
    catch {
      case e:Exception => ctx.line.endWithError(e)
    }
  }

  override def toString = "Dependency Resolution Phase"
}