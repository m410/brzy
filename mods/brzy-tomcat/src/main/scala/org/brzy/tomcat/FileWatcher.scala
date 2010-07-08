package org.brzy.tomcat

import actors.Actor._
import scala.collection.JavaConversions._
import java.io.File
import collection.mutable.ListBuffer
import name.pachler.nio.file._
import name.pachler.nio.file.StandardWatchEventKind._

/**
 * http://www.rgagnon.com/javadetails/java-0490.html
 *  http://jpathwatch.wordpress.com/
 *
 * @author Michael Fortin
 * @version $Id : $
 */
class FileWatcher(baseDir: File, compiler: ScalaCompiler) {
  val watchService: WatchService = FileSystems.getDefault.newWatchService
  val paths = makePaths(baseDir)
  println(" -- paths: " + paths)
  paths.foreach(_.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE))

  val monitor = actor {
    loop {
      val signalledKey: WatchKey = watchService.take
      val list = signalledKey.pollEvents
      signalledKey.reset

      list.foreach((e: WatchEvent[_]) => e.kind match {
        case StandardWatchEventKind.ENTRY_CREATE =>
          val context: Path = e.context.asInstanceOf[Path]
          println(" -- New File: " + context)
//          compiler.compile(baseDir)
        case StandardWatchEventKind.ENTRY_MODIFY =>
          val context: Path = e.context.asInstanceOf[Path]
          println(" -- Modify File: " + context)
//          compiler.compile(baseDir)
        case _ =>
          val context: Path = e.context.asInstanceOf[Path]
          println("[WARNING] -- Unknown Change: " + context)
      })
    }
  }

  def makePaths(file: File): List[Path] = {
    val buffer = ListBuffer[Path]()

    if (file.isDirectory) {
      println(" -- watch: " + file.getAbsolutePath)
      buffer += Paths.get(file.getAbsolutePath)
      file.listFiles.foreach(buffer ++= makePaths(_))
    }
        
    buffer.toList
  }
}