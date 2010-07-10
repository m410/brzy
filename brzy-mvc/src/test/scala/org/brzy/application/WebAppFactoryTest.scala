package org.brzy.application

import org.junit.Assert._
import org.junit.{Ignore, Test}
import org.scalatest.junit.JUnitSuite


class WebAppFactoryTest extends JUnitSuite {

  @Test @Ignore def testWebApplication = {
    val url = getClass.getClassLoader.getResource("brzy-webapp.b.yml")
    assertNotNull(url)
    val app = WebAppFactory.create(url,"development")
    assertNotNull(app)
  }
}