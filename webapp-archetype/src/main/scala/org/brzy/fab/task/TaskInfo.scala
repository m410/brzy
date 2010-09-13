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
package org.brzy.fab.task


import TaskType._
import java.lang.reflect.Method

/**
 * This captures the information about a single task or phase.  Including information needed to
 * execute it and it's dependencies.  It's created and used in the build archetype.
 *
 * @author Michael Fortin
 */
case class TaskInfo(
        name: String,
        taskType: TaskType,
        desc: String,
        inst: AnyRef,
        mthd: Method,
        seq: Seq[TaskInfo] = Seq[TaskInfo](),
        takesArgs: Boolean = false) {

  def doTask(args: Array[String]) = mthd.invoke(inst, args)

  def doTask = mthd.invoke(inst)

  override def toString = "TaskInfo[" + name + "]"
}

