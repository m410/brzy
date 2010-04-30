package org.brzy.shell

import java.net.{URL, URLConnection}
import java.io.{File, BufferedInputStream, BufferedOutputStream, FileOutputStream}
import org.brzy.config.{AppConfig, PluginConfig}

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class PluginDownload(outputDir:File, appConfig:AppConfig) {

  appConfig.plugins.foreach(download _)

  def download(plugin:PluginConfig) = {

    if(plugin.local_location != null) { // if it has a local location ignore it.
      val file = new File(plugin.local_location)

      if(!file.exists)
        error("No Local plugin at location: " + plugin.local_location)

      println("- Using local plugin: " + plugin.local_location)
    }
    else {
      val url =
        if(plugin.remote_location != null)  // if it has a remote location us that location
          new URL(plugin.remote_location)
        else  // else create the url form the config, and download it
           new URL("Not A Real URL")

      println("- Downloading plugin: " + plugin.local_location)
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
  }
}