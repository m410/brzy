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