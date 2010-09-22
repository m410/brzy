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


import org.brzy.fab.print.{Debug, Task => pTask, Conversation}

/**
 * Organizes the tasks into a callable sequence and runs them in order.
 *
 * @author Michael Fortin
 */
class TaskRunner(taskList: List[TaskInfo])(implicit convo: Conversation) {
  def run(args: Array[String]) = {
    convo.say(Debug("args:" + args.mkString("[",",","]")))

    val task:TaskInfo = taskList.find(_.name == args(0)) match {
      case Some(t) => t
      case _ => error("Could not find a task to execute with name: " + args(0))
    }

    if (task.takesArgs) {
      task.seq.reverse.foreach(t => {
        convo.subject(pTask(t.name)) {
          t.doTask
        }
      })
      convo.subject(pTask(task.name)) {
        convo.say(Debug("args.tail:" + args.tail.mkString("[",",","]")))
        task.doTask(args.tail)
      }
    }
    else {
      args.foreach(arg => {
        val multiTask:TaskInfo = taskList.find(_.name == args(0)) match {
          case Some(t) => t
          case _ => error("Could not find a task to execute with name: " + args(0))
        }
        multiTask.seq.reverse.foreach(t => {
          convo.subject(pTask(t.name)) {
            t.doTask
          }
        })
        convo.subject(pTask(multiTask.name)) {
          multiTask.doTask
        }
      })
    }

  }
}