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
    val url = getClass.getClassLoader.getResource("brzy-plugin.b.yml")
    val workDir= new File(new File(System.getProperty("java.io.tmpdir")),"junitwork")

    if(workDir.exists)
      workDir.trash()

    workDir.mkdirs
    assertTrue(workDir.exists)
    val plugin = new PluginConfig(new File(url.getFile))
    assertNotNull(plugin)
    assertEquals(0, workDir.listFiles.length)
    plugin.downloadAndUnzipTo(workDir)
    assertEquals(1, workDir.listFiles.length)
  }
}