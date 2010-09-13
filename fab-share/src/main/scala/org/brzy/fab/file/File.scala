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


import java.io.{File=>JFile}
import collection.mutable.ListBuffer

object File {
	def apply(path:String) = {
    val elements = path.split("/")
    var files = initFiles(path)
    elements.foreach(x=>files = appendPath(files,x))

    if(files.size == 1)
		  files(0)
    else
      error("Matched more than one file: " + files.mkString("[",",","]") )
    //new JFile(path)
	}

  def apply(base:JFile, path:String) = {
    val elements = path.split("/")
    var files = List(base)
    elements.foreach(x=>files = appendPath(files,x))

    if(files.size == 1)
		  files(0)
    else
      error("Matched more than one file: " + files.mkString("[",",","]") )
  }

  protected def initFiles(path:String) = {

    val jfile = if(path.startsWith("/")) // root
      if(util.Properties.isWin)
        new JFile(util.Properties.userDir.substring(0,3))
      else
        new JFile("/")
    else
      new JFile(util.Properties.userDir)
    List(jfile)
  }

  protected def appendPath(files:List[JFile],path:String) = {
    if(!path.contains("*"))
      files.map(f=>new JFile(f,path))
    else {
      val Pattern = path.replace("*","(.+)").r
      val fs = ListBuffer[JFile]()
      files.foreach(f=>{
        val subpaths = f.list.filter(str=> str match {
          case Pattern(x) => true
          case _ => false
        })
        subpaths.foreach(fs += new JFile(f,_))
      })
      fs.toList
    }
  }
}