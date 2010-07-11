package org.brzy.util

import org.junit.Test
import org.junit.Assert._
import org.brzy.util.FileUtils._
import java.io.{BufferedWriter, FileWriter, File}
import org.scalatest.junit.JUnitSuite


class FileUtilsTest extends JUnitSuite {

  @Test
  def testCopy = {
    val file = File.createTempFile("temp","temp")
    val writer = new BufferedWriter(new FileWriter(file))
    writer.write("Sample File")
    writer.close

    val outFile = File.createTempFile("temp2","temp")

    if(outFile.exists)
      outFile.delete

    file copyTo outFile
    assertTrue(outFile.exists)
  }
}