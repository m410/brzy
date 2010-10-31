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
package org.brzy.fab.phase


import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Ignore
import org.junit.Assert._
import org.brzy.fab.build.BuildContext
import org.brzy.fab.file.File
import org.brzy.fab.print.Conversation

class DocPhaseTest extends JUnitSuite {
  @Test @Ignore def testDocs = {
    val line = new Conversation(false,false)
    val context = new BuildContext(
        environment = "test",
		    line = line,
        sourceDir = File("src/main")
    )
    val phase = new DocPhase(context)
    try {
      phase.doDocument
    }
    finally {
      line.end
    }
  }
}