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
package org.brzy.fab.remote


import java.io.{BufferedInputStream, BufferedOutputStream, FileOutputStream, File}
import java.net.URL

/**
 * This is a collection of a few functions to augment the java.util.URL class.
 *
 * @author Michael Fortin
 */
object UrlUtils {

  class UrlWrapper(url:URL) {

    def downloadToDir(dir:File):File = {

      if(!dir.exists)
        dir.mkdirs
      
      val urlStr = url.toString
      val filename = urlStr.substring(urlStr.lastIndexOf("/") + 1, urlStr.length)
      val destinationFile = new File(dir, filename)

      if (!destinationFile.exists)
        destinationFile.createNewFile

      val bis = new BufferedInputStream(url.openStream)
      val bos = new BufferedOutputStream(new FileOutputStream(destinationFile))
      val buf: Array[Byte] = new Array[Byte](1024);
      var len: Int = 0;

      try {
        while ({len = bis.read(buf); len > -1})
          bos.write(buf, 0, len)
      }
      finally {
        bos.close
        bis.close
      }

      destinationFile
    }
  }

  implicit def wrappedURL(url:URL) = new UrlWrapper(url)
}