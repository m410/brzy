package org.brzy.webapp.application

import org.scalatest.FlatSpec


class WebAppProxySpec extends FlatSpec  with Fixture{

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
