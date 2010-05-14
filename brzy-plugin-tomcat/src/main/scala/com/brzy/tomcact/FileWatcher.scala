package org.brzy.tomcat

import actors.Actor._
import scala.collection.JavaConversions._
import java.io.{IOException, File}
import name.pachler.nio.file._
import org.slf4j.LoggerFactory

/**
 * http://www.rgagnon.com/javadetails/java-0490.html
 *  http://jpathwatch.wordpress.com/
 *
 * @author Michael Fortin
 * @version $Id : $
 */
class FileWatcher(baseDir: File, compiler: Compiler) {
  val watchService:WatchService = FileSystems.getDefault.newWatchService
  val watchedPath:Path  = Paths.get(baseDir.getAbsolutePath);
  val key: WatchKey = {
    try {
      watchedPath.register(watchService,
          StandardWatchEventKind.ENTRY_CREATE,
          StandardWatchEventKind.ENTRY_MODIFY,
          StandardWatchEventKind.ENTRY_DELETE)
    }
    catch {
      case uox: UnsupportedOperationException =>
        error("File watching not supported!")
      case iox: IOException =>
        error("I/O errors, " + iox.getMessage)
    }
  }

  val monitor = actor {
    loop {
      val signalledKey: WatchKey = watchService.take();
      val list = signalledKey.pollEvents();
      signalledKey.reset();

      list.foreach((e: WatchEvent[_]) => e.kind match {
        case StandardWatchEventKind.ENTRY_CREATE =>
          val context: Path = e.context.asInstanceOf[Path]
          println("New    File: " + context)
        case StandardWatchEventKind.ENTRY_DELETE =>
          val context: Path = e.context.asInstanceOf[Path]
          println("Delete File: " + context)
        case StandardWatchEventKind.ENTRY_MODIFY =>
          val context: Path = e.context.asInstanceOf[Path]
          println("Modify File: " + context)
        case _ =>
          val context: Path = e.context.asInstanceOf[Path]
          println("Unknown Change: " + context)
      })
    }
  }

}