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
package org.brzy.jms.mock

import org.brzy.jms.Queue


@Queue(destination="test.queue")
class MockJmsService {
  var gotIt = false

  def onMessage(msg:String) ={
    println("##### msg: "  + msg)
    gotIt = true
  }

  def didGetIt = gotIt
}