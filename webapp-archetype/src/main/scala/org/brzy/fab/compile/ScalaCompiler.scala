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
package org.brzy.fab.compile


import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import scala.tools.nsc.Global
import scala.tools.nsc.Settings
import scala.tools.nsc.reporters.ConsoleReporter


class ScalaCompiler(out:PrintWriter) extends Compiler {

  override def compile(sourceDirectory: File, bytecodeDirectory: File, classpath:Array[File]): Unit = {
    val messageCollector = new StringWriter
    val messageCollectorWrapper = new PrintWriter( messageCollector )

    val settings = generateSettings( bytecodeDirectory, buildClassPath( classpath ) )
    val reporter = new ConsoleReporter( settings, Console.in, out )
    val compiler = new Global( settings, reporter )

    val list = findFiles(sourceDirectory)
    ( new compiler.Run ).compile( list.map( _.toString ) )

    // Bail out if compilation failed
    if(reporter.hasErrors) {
      reporter.printSummary
      throw new RuntimeException( "Compilation failed:\n" + messageCollector.toString )
    }
  }

  private def error( message: String ): Unit = {
    out.write(message)
    throw new RuntimeException( "Compilation failed:\n" + message )
  }

  private def buildClassPath( files:Array[File] ): String = {
    val builder = new StringBuilder
		files.foreach(f=>builder.append(f.getAbsolutePath).append(":"))
		builder.toString
  }

  private def generateSettings( bytecodeDirectory: File, classpath: String ): Settings = {
    val settings = new Settings( error )
    settings.classpath.value = classpath
    settings.outdir.value = bytecodeDirectory.toString
    settings.deprecation.value = true
    settings.unchecked.value = true
    settings
  }

  private def findFiles( root: File ): List[File] = {
    if( root.isFile )
      List( root )
    else
      makeList( root.listFiles ).flatMap { f => findFiles( f ) }
  }

  private def makeList( a: Array[File] ): List[File] = {
    if( a == null )
      Nil
    else
      a.toList
  }
}