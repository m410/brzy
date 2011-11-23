package org.brzy.application

import org.scalatest.FlatSpec
import org.brzy.webapp.controller.Controller
import org.brzy.service.Service
import org.brzy.webapp.action.Action


class WebAppProxySpec extends FlatSpec {
  "A Webapp" should "proxy controller with injection" in  {
    val wc = WebAppConfiguration.runtime(env = "test", defaultConfig = "/brzy-webapp.test.b.yml")
    val webapp = new TestWebapp(wc)
    assert(webapp != null, "webapp can't be null")

    val ssize: Int = webapp.services.size
    assert(1 == ssize, "Services.size must equal 1, but it was " + ssize)
    
    val size: Int = webapp.controllers.size
    assert(1 == size, "Controllers.size must equal 1, but it was " + size)

  }
}

class TestWebapp(wc:WebAppConfiguration) extends WebApp(wc) {
  private lazy val myService = proxyInstance[FixtureService]()

  def makeServices = List(myService)

  def makeControllers = {
    val myController = proxyInstance[FixtureController](myService)
    List(myController)
  }
}

class FixtureService extends Service {
  def anything = "anything"
}

class FixtureController(val fixtureService:FixtureService) extends Controller("") {
  def actions = List.empty[Action]
}