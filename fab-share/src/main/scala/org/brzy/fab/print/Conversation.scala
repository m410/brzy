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
package org.brzy.fab.print


import jline._
import actors.Actor._
import actors.Exit

import java.io.{BufferedReader, PipedReader, PipedWriter, PrintWriter}

/**
 *
 */
class Conversation(verbose: Boolean, debug: Boolean) {
  protected[print] val reader = new ConsoleReader
  protected[print] val startTime = System.currentTimeMillis

  protected[print] var columnWidth = 10
  protected[print] var whiteLine = {
    val sb = new StringBuilder(reader.getTermwidth)
    for (space <- 0 to reader.getTermwidth - 1) sb.append(" ")
    sb.toString
  }

  protected[print] var currentTask: Option[Task] = Option(null)

  protected[print] val writer = new PipedWriter()
  protected[print] val pipe = new PipedReader()
  protected[print] val lineReader = new BufferedReader(pipe)
  writer.connect(pipe)

  protected[print] val readerListener = actor {
    loop {
      try {
        lineReader.readLine match {
          case s: String => say(Debug(s))
          case _ => exit
        }
      }
      catch {
        case e: Exception => exit
      }
    }
  }.start


  protected[print] def printCurrentTask = {
    currentTask match {
      case Some(x) => print({
        if (x.name.length > columnWidth)
          x.name.substring(0, columnWidth)
        else
          x.name + fill(x.name)
      } + "|")
      case _ => print("          |")
    }
  }


  protected[print] var errorFree = true

  protected[print] def fill(str: String) = {
    val sb = new StringBuilder()
    for (x <- str.length to columnWidth - 1) sb.append(" ")
    sb.toString
  }



  protected[print] def speak(msg: String) = {
    if (verbose) {
      println(msg)
    }
    else {
      val cleaned = if (msg.length > reader.getTermwidth - 22)
        msg.substring(0, reader.getTermwidth - 22).replaceAll(util.Properties.lineSeparator, "").trim
      else
        msg.replaceAll(util.Properties.lineSeparator, "").trim

      val length: Int = 21 + cleaned.length // todo fix magic number 27
      print(cleaned)
      reader.redrawLine
    }
  }

  protected[print] def clearLine = {
    if(!verbose)
      print(whiteLine)
      reader.redrawLine
  }

  val out = new PrintWriter(writer, true)

  def begin(actions: Array[String]): Unit = {
    print("     | Brzy fab(ricate) v0.1 | ")
    actions.foreach(e => {print(e); print(" ")})
    println("")
  }

  def subject(t: Tree)(exec: => Unit) {
    t match {
      case a: Task => currentTask = Some(a)
      case _ => // nothing
    }
    exec
    t match {
      case a: Task => currentTask = None
      case _ => // nothing
    }
  }

  def say(event: Speak): Unit = {
    event match {
      case Debug(e) =>
        if (debug) {
          clearLine
          print("debug|")
          printCurrentTask
          speak(event.message)
        }
      case e:Info =>
        clearLine
        print("info |")
        printCurrentTask

        if(e.sticky)
          println(e.message)
        else
        speak(event.message)

      case Warn(e) =>
        print("warn |")
        printCurrentTask
        println(event.message)
    }
  }

  def ask(q: Question): String = {
    reader.readLine(q.message)
  }

  def endWithError(e:Exception) = {
    errorFree = false
    if(!verbose)clearLine
    print("ERROR| ")
    println(if(e.getMessage != null) e.getMessage else "")
    e.printStackTrace
    close
  }

  def endWithError(e:String) = {
    errorFree = false
    val time = (System.currentTimeMillis - startTime) / 1000d
    print("ERROR| ")
    print(time)
    println("s ")
    println(e)
    close
  }

  def end: Unit = {
    if(errorFree) {
      val time = (System.currentTimeMillis - startTime) / 1000d
      print("     | fab(ricate) was Successful, ")
      print(time)
      println("s")
      close
    }
  }

  private[print] def close = {
    readerListener ! Exit
    out.close
    writer.close
    pipe.close
    lineReader.close
  }
}