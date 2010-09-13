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


import java.util.zip.{Deflater, CRC32, ZipEntry, ZipOutputStream}
import java.io.{BufferedInputStream, FileInputStream, FileOutputStream, File => JFile}

/**
 * Create a java Jar file
 * http://blogs.sun.com/CoreJavaTechTips/entry/creating_zip_and_jar_files
 * @author Michael Fortin
 */
object Jar {

  def apply(source: JFile, destination: JFile, manifest: Map[String, String]) = {
    val baseFileSize = source.getAbsolutePath.length + 1

    try {
      val stream = new FileOutputStream(destination)
      val out = new ZipOutputStream(stream)
//      val crc = new CRC32
      out.setLevel(Deflater.DEFAULT_COMPRESSION)
      val buffer = new Array[Byte](1024)

      addEntries(source.listFiles)

      def addEntries(files: Array[JFile]) {
        val crc = new CRC32

        files.foreach(file => {
          if (file.isDirectory && !file.isHidden && !file.getName.startsWith(".")) {
            crc.reset
            val zipEntry = new ZipEntry(file.getAbsolutePath.substring(baseFileSize) + "/")
            
            out.putNextEntry(zipEntry)
            addEntries(file.listFiles)
          }
          else if(file.isFile &&  !file.isHidden && !file.getName.startsWith(".")){
            val crc = new CRC32
            val entry = new ZipEntry(file.getAbsolutePath.substring(baseFileSize))
            crc.reset
            var nRead:Int = 0

            val bis = new FileInputStream(file)
            while ({nRead = bis.read(buffer);nRead} > 0)
                crc.update(buffer, 0, nRead);
            bis.close

            entry.setTime(file.lastModified)
            entry.setMethod(ZipEntry.STORED)
            entry.setCompressedSize(file.length())
            entry.setSize(file.length())
            entry.setCrc(crc.getValue())
            out.putNextEntry(entry)
            
            val in = new FileInputStream(file)
            while ({nRead = in.read(buffer, 0, buffer.length);nRead} > 0)
              out.write(buffer, 0, nRead)

            out.closeEntry
            in.close
          }
        })
      }

      out.close
      stream.close
      println("Adding completed OK")
    }
    catch {
      case e: Exception =>
        println("Error: " + e.getMessage())
        e.printStackTrace()
    }
    destination
  }
}