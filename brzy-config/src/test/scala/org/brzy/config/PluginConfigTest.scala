package org.brzy.config

import org.junit.Test
import org.junit.Assert._
import java.io.File
import org.brzy.util.FileUtils._

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class PluginConfigTest {

  @Test
  def testDownload() = {
    val url = getClass.getClassLoader.getResource("brzy-app.b.yml")
    val config = new Builder(url,"development").applicationConfig

    assertNotNull(config)
    assertNotNull(config.plugins)
    assertEquals(3,config.plugins.length)

    val tmpDir = new File(System.getProperty("java.io.tmpdir"))
    val workDir= new File(tmpDir,"junitwork")
    println("work dir: " + workDir)

    if(workDir.exists)
      workDir.trash()

    workDir.mkdirs
    assertTrue(workDir.exists)
    assertEquals(0, workDir.listFiles.length)
    
    assertEquals(2, workDir.listFiles.length)
  }
}