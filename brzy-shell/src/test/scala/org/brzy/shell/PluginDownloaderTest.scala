package org.brzy.shell

import org.junit.Test
import org.junit.Assert._
import java.io.File
import org.brzy.config.Builder

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class PluginDownloaderTest {

  @Test
  def downloadTest = {
    val url = getClass.getClassLoader.getResource("brzy-app.b.yml")
    val config = new Builder(url,"development").runtimeConfig
    assertEquals(5,config.plugins.length)
    val tmpDir = new File(System.getProperty("java.io.tmpdir"))
    val workDir= new File(tmpDir,"junitwork")
    println("work dir: " + workDir)

    if(workDir.exists)
      recursiveDelete(workDir)
    
    workDir.mkdirs
    assertTrue(workDir.exists)
    assertEquals(0, workDir.listFiles.length)
    new PluginDownloader(workDir,config)
    assertEquals(2, workDir.listFiles.length)
  }

  def recursiveDelete(file:File):Unit = {
    println("delete: " + file.getAbsolutePath)
    if(file.isDirectory) {
      file.listFiles.foreach(recursiveDelete _)
      file.delete
    }
    else
      file.delete
  }
}