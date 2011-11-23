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
    assert(webapp.myController != null,"myController is null")
    assert(webapp.myController.fixtureService != null,"myController.fixture is null")
    val size: Int = webapp.controllers.size
    assert(1 < size, "Controllers.size must equal 1, but it was " + size)
  }
}

class TestWebapp(wc:WebAppConfiguration) extends WebApp(wc) {
  val myService = proxyInstance[FixtureService]()
  val myController = proxyInstance[FixtureController](myService)

//  override val controllers = List(myController)
}

class FixtureService extends Service {
  def anything = "anything"
}

class FixtureController(val fixtureService:FixtureService) extends Controller("") {
  def actions = List.empty[Action]
}