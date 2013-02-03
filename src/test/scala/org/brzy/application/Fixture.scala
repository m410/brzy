package org.brzy.application

import org.brzy.service.Service
import org.brzy.controller.Controller


trait Fixture {

  class TestWebapp(wc:WebAppConfiguration) extends WebApp(wc) {
    override val controllers = List(new FixtureController with FixtureService)

  }

  trait FixtureService extends Service {
    def anything = "anything"
  }

  class FixtureController extends Controller("") {
    self: FixtureService =>
  }
}
