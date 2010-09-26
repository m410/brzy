package org.brzy.fab.cli.build

import org.brzy.fab.plugin.BuildPlugin
import org.brzy.fab.build.BuildContext
import org.brzy.fab.task.{Task, TaskInfo, TaskType}
import java.lang.reflect.Constructor

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
class PluginRunner(taskName:String, buildContext: BuildContext, pluginClass: Class[_]) {
  protected[build] val mtd = pluginClass.getMethod(taskName)
  protected[build] val taskAnno = mtd.getAnnotation(classOf[Task])
  protected[build] val takesArgs = mtd.getParameterTypes.length > 0
  protected[build] val plugin = {
    val constructor: Constructor[_] = pluginClass.getConstructor(Array(buildContext.getClass): _*)
    constructor.newInstance(buildContext).asInstanceOf[BuildPlugin]
  }

  val taskInfo = TaskInfo(name = taskAnno.name,
    taskType = TaskType.Plugin,
    desc = taskAnno.desc,
    inst = plugin,
    mthd = mtd,
    takesArgs = takesArgs)

  def run: Unit = {
    taskInfo.doTask
  }
}