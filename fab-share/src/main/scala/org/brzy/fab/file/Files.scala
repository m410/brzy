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
package org.brzy.fab.file


import java.io.{File => JFile}
import collection.mutable.ListBuffer

/**
 * Helper Class to find files on the file system using ant style expressions.
 */
object Files {
  def apply(path: String): List[JFile] = {
    var files = initFiles(path)
    path.split("/").foreach(x => {files = appendPath(files, x)})
    files.toList
  }

  def apply(base: JFile, path: String): List[JFile] = {
    var files = List(base)
    path.split("/").foreach(x => {files = appendPath(files, x)})
    files.toList
  }

  protected def initFiles(path: String) = {
    val jfile = if (path.startsWith("/")) // root
      if (util.Properties.isWin)
        new JFile(util.Properties.userDir.substring(0, 3))
      else
        new JFile("/")
    else
      new JFile(util.Properties.userDir)

    List(jfile)
  }

  protected def appendPath(files: List[JFile], path: String): List[JFile] = {
    if (!path.contains("*")) {
      files.map(f => new JFile(f, path))
    }
    else if (path.equals("*")) {
      val fs = ListBuffer[JFile]()

      files.filter(f=>f.isDirectory && !f.isHidden && !f.getName.startsWith(".")).foreach(f => {
        f.listFiles.filter(_.isDirectory).filter(!_.isHidden).foreach(sub => {
          fs.append(sub)
        })
      })
      fs.toList
    }
    else if (path.equals("**")) {
      error("Double star (**) wildcards are not supported yet.")
    }
    else { // path = * plus some value
      val Pattern = path.replace("*", "(.+)").r
      val fs = ListBuffer[JFile]()
      files.foreach(f => {
        if (f.isDirectory && !f.isHidden && !f.getName.startsWith(".")) {
          val subpaths = f.list.filter(str => str match {
            case Pattern(x) => true
            case _ => false
          })
          subpaths.foreach(fs += new JFile(f, _))
        }
      })
      fs.toList
    }
  }
}