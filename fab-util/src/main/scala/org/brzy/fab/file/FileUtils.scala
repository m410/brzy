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



import java.util.zip.{ZipEntry, ZipFile}
import java.io._

/**
 * this is a collection of a vew implicit functions that are added to java.util.File
 *
 * @author Michael Fortin
 */
object FileUtils {

	/**
	 *  File Wrapper class.
	 */
  class FileWrapper(file:File) {

    /**
     * from file to directory = new sub file with sub folders
     * from directory to directory = new sub directory recursively with sub folders
     * from directory to existing file = error
     * from file to existing file = error
     * from file to non existing file = new file with sub folders
     *
     * converts unix paths to dos paths if on window.
     * defaults to unix paths.
     */
    def copyTo(toFile:File):Unit = {

      if (file.isDirectory) {
        val dir = new File(toFile, file.getName)
        dir.mkdir
        file.listFiles.foreach(f => new FileWrapper(f).copyTo(dir))
      }
      else {
        val destFile = new File(toFile, file.getName)

        if(!destFile.exists) {
          destFile.getParentFile.mkdirs
          destFile.createNewFile
        }

        val source = new FileInputStream(file).getChannel
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

    def moveTo(toFile:File):Unit = {

    }

    def unzip():Unit = {
      val parent = file.getParentFile
      val zipFile:ZipFile  = new ZipFile(file)
      val e = zipFile.entries.asInstanceOf[java.util.Enumeration[ZipEntry]]

      while (e.hasMoreElements) {
        val entry = e.nextElement.asInstanceOf[ZipEntry]
        val is = new BufferedInputStream (zipFile.getInputStream(entry))
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

    def zip = {

    }

    def trash():Unit = {
      if(file.isDirectory) {
        file.listFiles.foreach(new FileWrapper(_).trash())
        file.delete
      }
      else
        file.delete
    }
  }

  implicit def wrappedFile(file:File) = new FileWrapper(file)

}