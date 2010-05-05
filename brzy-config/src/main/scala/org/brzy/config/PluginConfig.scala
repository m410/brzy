package org.brzy.config

import reflect.BeanProperty
import java.net.{URL, URLConnection}
import java.io.{FileOutputStream, BufferedInputStream, BufferedOutputStream, File}
import org.slf4j.LoggerFactory
import org.brzy.util.FileUtils._

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class PluginConfig(file:File) extends MergeConfig[PluginConfig] {

  def this() = this(null)

  private val log = LoggerFactory.getLogger(getClass)
  
  @BeanProperty var application:Application = _
  @BeanProperty var name:String = _
  @BeanProperty var implementation:String = _
  @BeanProperty var remote_location:String = _
  @BeanProperty var local_location:String = _
  @BeanProperty var version:String = _
  @BeanProperty var scan_package:String = _
  @BeanProperty var properties:java.util.HashMap[String,String] =_

  if(file != null) {
    // load from file
  }

  def +(that: PluginConfig) = {
    val proj = new PluginConfig
    proj.name = if(that.name != null) that.name else name
    proj.implementation = if(that.implementation != null) that.implementation else implementation
    proj.version = if(that.version != null) that.version else version
    proj.scan_package = if(that.scan_package != null) that.scan_package else scan_package
    proj.properties = if(that.properties != null) that.properties else properties
    proj
  }

  /**
   * This downloads the plugin and expands it in the output directory unless this
   * plugin has a local_location.  In which case the local location is used instead
   * of the plugin cache.
   */
  def downloadTo(outputDir:File) =
    // if it has a local location ignore it.
    if (local_location != null) {

      val file = new File(local_location)

      if (!file.exists)
        error("No Local plugin at location: " + local_location)
    }
    // downloads from web
    else if (remote_location != null && remote_location.startsWith("http")) {
      val url = new URL(remote_location)
      val urlc: URLConnection = url.openConnection
      val destinationFolder = new File(outputDir, name)

      if (!destinationFolder.exists)
        destinationFolder.mkdirs

      val filename = remote_location
              .substring(remote_location.lastIndexOf("/"), remote_location.length)
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
    // copy from local file system
    else if (remote_location != null && !remote_location.startsWith("http")) {
      val remoteLoc: String = remote_location

      val sourceFile =
        if(remoteLoc.startsWith("~")){
          val home = new File(System.getProperty("user.home"))
          new File(home,remoteLoc.substring(1,remoteLoc.length))
        }
        else
          new File(remoteLoc)

      val destinationFolder = new File(outputDir, name)

      if(!destinationFolder.exists)
        destinationFolder.mkdirs

      val filename = remoteLoc.substring(remoteLoc.lastIndexOf("/"), remoteLoc.length)
      val destinationFile = new File(destinationFolder, filename)

      println("source:" + sourceFile.getAbsolutePath)
      println("dest:" + destinationFile.getAbsolutePath)
      sourceFile.copyTo(destinationFolder)
      destinationFile.unzip()
    }
    // deducte the external path and downlaod it
    else { // neither local or remote locations, need to make url
      // TODO need to implement this
      // error("Unknown file Location for plugin: '" + name + "'")
    }



  override def toString = {
    val newline = System.getProperty("line.separator")
    val sb = new StringBuilder()
    sb.append(newline)
    sb.append("  plugin")append(newline)
    sb.append("   - name").append("=").append(name).append(newline)
    sb.append("   - implementation").append("=").append(implementation).append(newline)
    sb.append("   - version").append("=").append(version).append(newline)
    sb.append("   - scan_package").append("=").append(scan_package).append(newline)
    sb.append("   - properties").append("=").append(properties).append(newline)
    sb.toString
  }
}