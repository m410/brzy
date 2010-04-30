package org.brzy.shell

import org.brzy.config.PluginConfig
import java.net.{URL, URLConnection}
import java.io.{File, BufferedInputStream, BufferedOutputStream, FileOutputStream}

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class PluginDownload(plugin:PluginConfig) {

  val url:URL = new URL("")
  val urlc:URLConnection = url.openConnection
  val destination = new File("")
  val bis = new BufferedInputStream(urlc.getInputStream())
  val bos = new BufferedOutputStream(new FileOutputStream(destination))
  var i:Int = 0
  while ((i = bis.read()) != -1)
     bos.write( i )

  bos.close
  bis.close
}