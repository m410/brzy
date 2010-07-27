package org.brzy.util

import java.io.{BufferedInputStream, BufferedOutputStream, FileOutputStream, File}
import java.net.URL

/**
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