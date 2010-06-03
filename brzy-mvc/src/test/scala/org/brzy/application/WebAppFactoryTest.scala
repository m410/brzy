package org.brzy.application

import org.junit.Assert._
import org.junit.{Ignore, Test}
import org.scalatest.junit.JUnitSuite

/**
 * @author Michael Fortin
 * @version $Id : $
 */
class WebAppFactoryTest extends JUnitSuite {

  @Test
  @Ignore
  def testWebApplication = {
    val url = getClass.getClassLoader.getResource("brzy-webapp.b.yml")
    assertNotNull(url)
    val app = null// WebAppFactory.create(new Builder(url, "development").runtimeConfig)
    assertNotNull(app)
  }
}