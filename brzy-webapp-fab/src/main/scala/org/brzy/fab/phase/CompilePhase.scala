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
import org.brzy.fab.file.FileUtils._
import org.brzy.fab.compile.ScalaCompiler
import org.brzy.fab.file.Files
import java.io._

/**
 * 	process-resources
 * @author Michael Fortin
 */
class CompilePhase(ctx: BuildContext) {
  def processResources = {
    ctx.line.say(Debug("process-resources"))

    val classes = new File(ctx.targetDir, "classes")
    classes.mkdirs

    val resources = new File(ctx.sourceDir, "resources")

    if (resources.exists) {
      resources.listFiles.foreach(_.copyTo(classes))
    }
  }

  def compile = {
    ctx.line.say(Debug("compile-task"))

    try {
      val writer = new StringWriter()
      val w = new PrintWriter(writer)

      val compiler = new ScalaCompiler(w)
//      val compiler = new ScalaCompiler(ctx.line.out)
      val classpath = Files(".brzy/app/compile/*.jar")
      val outputDir = new File(ctx.targetDir, "classes")
      val sourceDir = new File(ctx.sourceDir, "scala")
      classpath.foreach(cp => ctx.line.say(Debug("cp: " + cp)))
      ctx.line.say(Debug("source: " + sourceDir))
      ctx.line.say(Debug("target: " + outputDir))

      if (!compiler.compile(sourceDir, outputDir, classpath.toArray))
        ctx.line.endWithError(writer.toString)
    }
    catch {
      case unknown:Exception => ctx.line.endWithError(unknown)
    }
  }


  override def toString = "Compile Phase"
}