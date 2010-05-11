package org.brzy.config

import reflect.BeanProperty
import java.net.URL
import java.io.File
import org.slf4j.LoggerFactory
import org.brzy.util.FileUtils._
import org.brzy.util.UrlUtils._
import org.ho.yaml.Yaml
import collection.mutable.ArrayBuffer

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class PluginConfig(file:File) extends MergeConfig[PluginConfig] {

  def this() = this(null)

  private val log = LoggerFactory.getLogger(getClass)
  
  @BeanProperty var application:Application = _
  @BeanProperty var name:String = _
  @BeanProperty var version:String = _
  @BeanProperty var group_id:String = _

  @BeanProperty var implementation:String = _
  @BeanProperty var remote_location:String = _
  @BeanProperty var local_location:String = _

  @BeanProperty var scan_package:String = _
  @BeanProperty var properties:java.util.HashMap[String,String] =_

  @BeanProperty var web_xml:java.util.ArrayList[java.util.HashMap[String,java.lang.Object]] = _
  @BeanProperty var repositories:Array[Repository] = _
  @BeanProperty var dependencies:Array[Dependency] = _


  if(file != null) {
    val config:PluginConfig = Yaml.loadType(file, classOf[PluginConfig])

    application = config.application

    if(remote_location == null)
      remote_location = config.remote_location

    if(local_location == null)
      local_location = config.local_location

    if(version == null)
      version = config.version

    if(group_id == null)
      group_id = config.group_id

    if(name == null)
      name = config.name

    if(scan_package != null)
      scan_package = config.scan_package

    if(properties == null)
      properties = new java.util.HashMap[String,String]

    if(config.properties != null)
      properties putAll config.properties

    if(web_xml == null)
      web_xml = new java.util.ArrayList[java.util.HashMap[String,java.lang.Object]]

    if(config.web_xml != null)
      web_xml addAll config.web_xml

    if(dependencies == null)
      dependencies = Array()

    if(config.dependencies != null)
      dependencies ++= config.dependencies

    if(repositories == null)
      repositories = Array()

    if(config.repositories != null)
      repositories ++= config.repositories
  }

  def +(that: PluginConfig) = {
    val proj = new PluginConfig
    proj.name = if(that.name != null) that.name else name
    proj.implementation = if(that.implementation != null) that.implementation else implementation
    proj.version = if(that.version != null) that.version else version
    proj.group_id = if(that.group_id != null) that.group_id else group_id
    proj.scan_package = if(that.scan_package != null) that.scan_package else scan_package
    proj.properties = if(that.properties != null) that.properties else properties

    val repos = new ArrayBuffer[Repository]()

    if(repositories != null)
      repos ++= repositories

    if(that.repositories != null)
        repos ++= that.repositories

    proj.repositories = repos.toArray

    val deps = new ArrayBuffer[Dependency]()

    if(dependencies != null)
      deps ++= dependencies

    if(that.dependencies != null)
        deps ++= that.dependencies

    proj.dependencies = deps.toArray

    proj.web_xml = new java.util.ArrayList[java.util.HashMap[String,java.lang.Object]]

    if(web_xml!= null)
      proj.web_xml addAll web_xml

    if(that.web_xml!= null)
      proj.web_xml addAll that.web_xml

    proj
  }

  /**
   * This downloads the plugin and expands it in the output directory unless this
   * plugin has a local_location.  In which case the local location is used instead
   * of the plugin cache.
   */
  def downloadAndUnzipTo(outputDir:File) =
    // if it has a local location ignore it.
    if (local_location != null) {
      val file = new File(local_location)

      if (!file.exists)
        error("No Local plugin at location: " + local_location)
    }
    // downloads from web
    else if (remote_location != null && remote_location.startsWith("http")) {
      val destinationFile = new URL(remote_location).downloadToDir(outputDir)
      destinationFile.unzip()
    }
    // copy from local file system
    else  {
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

      sourceFile.copyTo(destinationFolder)
      destinationFile.unzip()
    }

  def downloadAndUnzipTo(remoteUrl:String, appPluginCache:File) = {
    val destinationFile = new URL(remoteUrl).downloadToDir(new File(appPluginCache,name))
    destinationFile.unzip()
  }
}