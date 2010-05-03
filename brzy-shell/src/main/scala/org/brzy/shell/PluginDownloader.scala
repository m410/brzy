package org.brzy.shell

import java.net.{URL, URLConnection}
import org.brzy.config.{AppConfig, PluginConfig}
import java.io._
import java.util.zip.{ZipFile, ZipEntry}
import java.util.Enumeration
import java.lang.String

/**
 * @author Michael Fortin
 * @version $Id : $
 */
class PluginDownloader(outputDir: File, appConfig: AppConfig) {
  appConfig.plugins.foreach(download _)

  def download(plugin: PluginConfig) =
    if (plugin.local_location != null) { // if it has a local location ignore it.
      val file = new File(plugin.local_location)

      if (!file.exists)
        error("No Local plugin at location: " + plugin.local_location)
    }
    else if (plugin.remote_location != null && plugin.remote_location.startsWith("http")) {
      val url = new URL(plugin.remote_location)
      val urlc: URLConnection = url.openConnection
      val destinationFolder = new File(outputDir, plugin.name)

      if (!destinationFolder.exists)
        destinationFolder.mkdirs

      val filename = plugin.remote_location
              .substring(plugin.remote_location.lastIndexOf("/"), plugin.remote_location.length)
      val destinationFile = new File(destinationFolder, filename)

      if (!destinationFile.exists)
        destinationFile.createNewFile

      println("dest file: " + destinationFile.getAbsolutePath)

      val bis = new BufferedInputStream(urlc.getInputStream())
      val bos = new BufferedOutputStream(new FileOutputStream(destinationFile))
      val buf: Array[Byte] = new Array[Byte](1024);
      var len: Int = 0;
      while ({len = bis.read(buf); len > -1})
        bos.write(buf, 0, len);

      bos.close
      bis.close
    }
    else if (plugin.remote_location != null && !plugin.remote_location.startsWith("http")) {
      val remoteLoc: String = plugin.remote_location

      val sourceFile =
        if(remoteLoc.startsWith("~")){
          val home = new File(System.getProperty("user.home"))
          new File(home,remoteLoc.substring(1,remoteLoc.length))
        }
        else
          new File(remoteLoc)

      val destinationFolder = new File(outputDir, plugin.name)

      if(!destinationFolder.exists)
        destinationFolder.mkdirs
      
      val filename = remoteLoc.substring(remoteLoc.lastIndexOf("/"), remoteLoc.length)
      val destinationFile = new File(destinationFolder, filename)

      println("source:" + sourceFile.getAbsolutePath)
      println("dest:" + destinationFile.getAbsolutePath)
      recursiveCopy(sourceFile, destinationFolder)
      unzip(destinationFile)
    }
    else { // neither local or remote locations, need to make url
//      error("Unknown file Location for plugin: '" + plugin.name + "'")
    }

  def recursiveCopy(sourceFile: File, dest: File): Unit = {

    if (sourceFile.isDirectory) {
      val dir = new File(dest, sourceFile.getName)
      dir.mkdir
      sourceFile.listFiles.foreach(f => recursiveCopy(f, dir))
    }
    else {
      val destFile = new File(dest, sourceFile.getName)
      val source = new FileInputStream(sourceFile).getChannel
      val destination = new FileOutputStream(destFile).getChannel

      try {
        destination.transferFrom(source, 0, source.size())
      }
      finally {
        if (source != null)
          source.close

        if (destination != null)
          destination.close
      }
    }
  }

  def unzip(file: File): Unit = {
    val parent = file.getParentFile
    val zipfile:ZipFile  = new ZipFile(file)
    val e = zipfile.entries.asInstanceOf[java.util.Enumeration[ZipEntry]]

    while (e.hasMoreElements) {
      val entry = e.nextElement.asInstanceOf[ZipEntry]
      val is = new BufferedInputStream (zipfile.getInputStream(entry))
      var count:Int = 0

      if(entry.isDirectory)
        new File(parent, entry.getName()).mkdirs
      else {
        val data:Array[Byte] = new Array[Byte](1024)
        val outFile: File = new File(parent, entry.getName())
        val fos = new FileOutputStream(outFile)
        val dest = new BufferedOutputStream(fos, 1024)

        while ({count = is.read(data, 0, 1024); count > -1})
          dest.write(data, 0, count);

        dest.close
        is.close
      }
    }
  }
}