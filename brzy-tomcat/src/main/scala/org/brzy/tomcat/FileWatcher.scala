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
package org.brzy.tomcat

import actors.Actor._
import actors.{Exit, TIMEOUT}

import java.io.File
import org.brzy.fab.compile.{Compiler => SCompiler}
import org.brzy.fab.file.Files


/**
 * http://www.rgagnon.com/javadetails/java-0490.html
 *  http://jpathwatch.wordpress.com/
 *
 * @author Michael Fortin
 */
class FileWatcher(baseDir: File, destDir: File, libDir: File, compiler: SCompiler) {
  val paths = findFiles(baseDir)
  val libs = Files(libDir, "*.jar").toArray
  private[this] var lastModified: Long = System.currentTimeMillis

  private[this] val watcher = actor {
    loop {
      reactWithin(1000) {
        case TIMEOUT =>
          paths.find(_.lastModified > lastModified) match {
            case Some(f) =>
              lastModified = System.currentTimeMillis
              val success = compiler.compile(baseDir, destDir, libs)
              f
            case _ => // nothing
          }
        case Exit => exit()
      }
    }
  }.start

  private def findFiles(root: File): List[File] = {
    if (root.isFile && root.getName.endsWith(".scala"))
      List(root)
    else
      makeList(root.listFiles).flatMap {f => findFiles(f)}
  }

  private def makeList(a: Array[File]): List[File] = {
    if (a == null)
      Nil
    else
      a.toList
  }
}