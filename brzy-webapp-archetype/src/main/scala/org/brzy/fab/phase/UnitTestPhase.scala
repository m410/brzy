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
import org.brzy.fab.task.Task
import org.brzy.fab.file.FileUtils._
import org.brzy.fab.file.{File, Files}
import org.brzy.fab.compile.ScalaCompiler
import org.brzy.fab.print.{Info, Warn, Debug}
import org.scalatest.tools.Runner

/**
 * @author Michael Fortin
 */
@Phase(name = "test", desc = "Compile and run unit tests", defaultTask = "test-task", dependsOn = Array("compile"))
class UnitTestPhase(ctx: BuildContext) {

  @Task(name = "process-test-resources", desc = "Unit test pre processing")
  def processTestResources = {
    ctx.line.say(Debug("process-test-resources"))
    val classes = File(ctx.targetDir, "test-classes")
    classes.mkdirs
    val resources = File(ctx.testDir, "resources")

    if (resources.exists)
      resources.listFiles.foreach(_.copyTo(classes))
  }

  @Task(name = "test-compile", desc = "Compile Unit tests", dependsOn = Array("process-test-resources"))
  def testCompile = {
    ctx.line.say(Debug("test-compile"))
    val compiler = new ScalaCompiler(ctx.line.out)
    val jars = Files(".brzy/app/lib/test/*.jar")
    val classpath = Files(ctx.testDir, "resources/*") ++ Files(ctx.targetDir, "classes") ++ jars
    val outputDir = File(ctx.targetDir, "test-classes")
    val sourceDir = File(ctx.testDir, "scala")
    classpath.foreach(cp=>ctx.line.say(Debug("cp: " + cp)))
    ctx.line.say(Debug("source: " + sourceDir))
    ctx.line.say(Debug("target: " + outputDir))

    if(!compiler.compile(sourceDir,outputDir,classpath.toArray))
      ctx.line.endWithError("Compilation Failed")
  }

  @Task(name = "test-task", desc = "Run unit tests", dependsOn = Array("test-compile"))
  def testPhase = {
    ctx.line.say(Debug("test-task"))

    val output = File("target/test-reports/")
    output.mkdirs
    val outputDir = output.getAbsolutePath

    val classpath = List(File(ctx.targetDir, "classes")) ++
        List(File(ctx.targetDir, "test-classes")) // ++ Files(".brzy/app/lib/test/*.jar")
    classpath.foreach(cp=>ctx.line.say(Debug("test cp: " + cp)))
    val testArgs  = Array(
        "-o",
        "-f", File(output,"report.out.txt").getAbsolutePath,
        "-u", output.getAbsolutePath,
        "-p", classpath.map(_.getAbsolutePath).mkString(" "))
    Runner.run(testArgs)
  }

  override def toString = "Test Phase"
}