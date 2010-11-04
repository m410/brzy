package org.brzy.fab.phase

import org.brzy.fab.build.BuildContext
import java.io.{BufferedWriter, FileWriter}
import org.brzy.fab.print.{Question, Debug}
import org.brzy.fab.file.File
import org.clapper.scalasti.StringTemplateGroup

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
class ControllerGenPlugin(ctx:BuildContext) {
  def genController = {
    ctx.line.say(Debug("gen-controller"))
    val packageAndClass = ctx.line.ask(Question("enter package & class: "))
		val className = packageAndClass.substring(packageAndClass.lastIndexOf(".") +1)
    val packageName = packageAndClass.substring(0,packageAndClass.lastIndexOf("."))
		// ask if you want to add the controller
		// ask to create unit test

    val brzy = ctx.brzyHomeDir
		val group = new StringTemplateGroup("mygroup", File(brzy,"archetypes/brzy-webapp/templates"))
		val template = group.template("controller-scala")
		template.setAttribute("packageName",packageName)
		template.setAttribute("className",className)
		val outputFile = File("src/scala/" + className + ".scala")
		val writer = new BufferedWriter(new FileWriter(outputFile))
		writer.write(template.toString)
  }

  override def toString = "Generate Controller"
}