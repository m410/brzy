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


import org.brzy.fab.plugin.{Plugin, BuildPlugin}
import collection.mutable.{HashMap, ListBuffer}
import org.brzy.fab.phase.Phase

/**
 * Builds the call sequence for each task.
 *
 * @author Michael Fortin
 */
class SeqBuilder(plugins: List[BuildPlugin], phases: List[AnyRef]) {
  val taskList = {
    val nameTaskMap = HashMap[String, TaskInfo]()
    addPhases(nameTaskMap)
    addPlugins(nameTaskMap)

    val outList = ListBuffer[TaskInfo]()
    nameTaskMap.values.foreach(nvp => {
      val depends = traverseTask(Seq[String](), List(nvp))
      outList += nvp.copy(seq = depends.map(nameTaskMap(_)))
    })

    def traverseTask(dependentSeq: Seq[String], taskList: List[TaskInfo]): Seq[String] = {
      var depSeq = Seq[String]()

      taskList.foreach(taskInfo => {
        if (taskInfo.taskType == TaskType.Phase) {
          val taskDeps = taskInfo.mthd.getAnnotation(classOf[Task]).dependsOn
          taskDeps.filter(d=>d != "" && !dependentSeq.contains(d)).foreach(x => {
            depSeq = traverseTask(depSeq :+ x, List(nameTaskMap(x)))
          })
          val phaseDeps = taskInfo.mthd.getDeclaringClass.getAnnotation(classOf[Phase]).dependsOn
          phaseDeps.filter(d=>d != "" && !dependentSeq.contains(d) && !depSeq.contains(d)).foreach(x => {
            depSeq = traverseTask(depSeq :+ x, List(nameTaskMap(x)))
          })
        }
        else {
          val taskAnno = taskInfo.mthd.getAnnotation(classOf[Task])
          taskAnno.dependsOn.filter(d=>d != "" && !dependentSeq.contains(d)  && !depSeq.contains(d)).foreach(x => {
            depSeq = traverseTask(depSeq :+ x, List(nameTaskMap(x)))
          })
        }
      })
      dependentSeq ++ depSeq
    }

    outList.toList
  }


  protected[task] def addPhases(nameTaskMap: HashMap[String, TaskInfo]): Unit = {
    phases.foreach(phase => {
      val clazz = phase.getClass
      val phaseAnno = clazz.getAnnotation(classOf[Phase])
      val dMethod = clazz.getMethods.find(m => {
        m.getAnnotation(classOf[Task]) != null &&
                m.getAnnotation(classOf[Task]).name == phaseAnno.defaultTask
      }) match {
        case Some(mtd) => mtd
        case _ => error("no task annotated with name: " + phaseAnno.defaultTask)
      }

      val dTaskAnno = dMethod.getAnnotation(classOf[Task])
      val dDepend = dTaskAnno.dependsOn
      nameTaskMap += phaseAnno.name -> TaskInfo(phaseAnno.name, TaskType.Phase, phaseAnno.desc, phase, dMethod)

      clazz.getMethods.foreach(mtd => {
        val taskAnno = mtd.getAnnotation(classOf[Task])

        if (taskAnno != null) {
          val depends = if (taskAnno.dependsOn.length > 0)
            Option(taskAnno.dependsOn)
          else
            Option(null)
          nameTaskMap += taskAnno.name -> TaskInfo(taskAnno.name, TaskType.Task, taskAnno.desc, phase, mtd)
        }

      })
    })
  }

  protected[task] def addPlugins(nameTaskMap: HashMap[String, TaskInfo]): Unit = {
    plugins.foreach(plugin => {
      val clazz = plugin.getClass
      val pluginAnno: Plugin = clazz.getAnnotation(classOf[Plugin])

      clazz.getMethods.foreach(mtd => {
        val taskAnno = mtd.getAnnotation(classOf[Task])
        val takesArgs = mtd.getParameterTypes.length > 0
        if (taskAnno != null) {

          nameTaskMap += taskAnno.name ->
                  TaskInfo(name = taskAnno.name,
                    taskType = TaskType.Task,
                    desc = taskAnno.desc,
                    inst = plugin,
                    mthd = mtd,
                    takesArgs = takesArgs)
        }
      })
    })
  }
}

object SeqBuilder {
  def apply(plugins: List[BuildPlugin], phases: List[AnyRef]) = new SeqBuilder(plugins, phases)
}