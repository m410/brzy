package org.brzy.webapp.application

import org.brzy.webapp.service.Service
import org.brzy.webapp.controller.BaseController


trait Fixture {

  class TestWebapp(wc:WebAppConfiguration) extends WebApp(wc) {
    override val controllers = List(new FixtureController with FixtureService)
    override val services = List(new FixtureService{})
  }

  trait FixtureService extends Service {
    def anything = "anything"
  }

  class FixtureController extends BaseController("") {
    self: FixtureService =>
  }
}
