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