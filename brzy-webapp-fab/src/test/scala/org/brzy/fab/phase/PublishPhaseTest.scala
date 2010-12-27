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
import org.junit.Assert._
import org.brzy.fab.file.File
import org.brzy.fab.build.BuildContext
import org.brzy.fab.print.Conversation
import org.brzy.application.WebAppConf
import org.junit.{Ignore, Test}

class PublishPhaseTest extends JUnitSuite {
  @Test @Ignore def testPublish = {
    val config = WebAppConf(env = "test", defaultConfig = "/brzy-webapp.test1.b.yml")
    val conversation = new Conversation(false, false)
    val homeDir = File("target/test-temp")
    homeDir.mkdirs
    val targetDir = File("target/target-temp")
    targetDir.mkdirs
    val sourceDir = File("target/source-temp")
    sourceDir.mkdirs

    val ctx = BuildContext(
        environment = "test",
        line = conversation,
        targetDir = targetDir,
        sourceDir = sourceDir,
        brzyHomeDir = homeDir)
    ctx.properties("webAppConfig") = config

    val phase = new PublishPhase(ctx)
    phase.publish
  }
}