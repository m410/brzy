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
import actors.Exit
import actors.Actor._
import org.brzy.webapp.action.returns.Error

/**
 *	process-resources
 * @author Michael Fortin
 */
class CompilePhase(ctx:BuildContext) {

  def processResources = {
    ctx.line.say(Debug("process-resources"))

    val classes = new File(ctx.targetDir, "classes")
    classes.mkdirs

    val resources = new File(ctx.sourceDir,"resources")

    if(resources.exists){
      resources.listFiles.foreach(_.copyTo(classes))
    }
  }

  def compile = {
    ctx.line.say(Debug("compile-task"))

    val writer = new PipedWriter()
    val reader = new PipedReader(writer)
    val sb = new StringBuilder()

    val bufWriter = actor {
      loop {
        case Exit =>
          writer.close
          reader.close
          exit
        case _ =>
          var chr:Int = null
          while({chr = reader.read;char} > 0)
            sb.append(char.asInstanceOf[Char])
      }
    }.start
    
    val compiler = new ScalaCompiler(new PrintWriter(writer, true))
    val classpath = Files(".brzy/app/compile/*.jar")
    val outputDir = new File(ctx.targetDir, "classes")
    val sourceDir = new File(ctx.sourceDir, "scala")
    classpath.foreach(cp=>ctx.line.say(Debug("cp: " + cp)))
    ctx.line.say(Debug("source: " + sourceDir))
    ctx.line.say(Debug("target: " + outputDir))
    
    if(!compiler.compile(sourceDir,outputDir,classpath.toArray)) {
      bufWriter ! Exit
      ctx.line.say(Error(sb.toString))
      ctx.line.endWithError("Compilation Failed")      
    }
    else {
      bufWriter ! Exit
    }

  }


  override def toString = "Compile Phase"
}