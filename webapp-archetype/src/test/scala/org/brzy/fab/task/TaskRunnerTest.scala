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
package org.brzy.fab.task


import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import org.brzy.fab.phase.Phase
import org.brzy.fab.build.BuildContext
import org.brzy.fab.print.{Debug, Conversation}
import org.junit.{Ignore, Test}

class TaskRunnerTest extends JUnitSuite {
  @Test @Ignore def testRun = {
    val conversation = new Conversation(false, false)
    implicit val talk = conversation

    val mockPhase = new MockPhase(new BuildContext("dev", talk))
    val mockInfo = TaskInfo(
        name = "hello-phase",
        taskType = TaskType.Phase,
        desc = "desc",
        inst = mockPhase,
        mthd = mockPhase.getClass.getMethod("helloTask"))
    val list = List(mockInfo)

    val runner = new TaskRunner(list)
    runner.run(Array("hello-phase"))
    conversation.end
  }
}

@Phase(name = "hello-phase", defaultTask = "hello1",desc="desc")
class MockPhase(ctx: BuildContext) {
  @Task(name = "hello1",desc="")
  def helloTask = {
    ctx.line.say(Debug("hello"))
  }
}

//@Phase(name = "hello-depend", defaultTask = "hello2", dependsOn="hello-phase",description="desc")
//class Mock2Phase(ctx: BuildContext) {
//  @Task(name = "hello2", description = "Unit test pre processing")
//  def helloTask = {
//    ctx.line.say(Debug("hello"))
//  }
//}