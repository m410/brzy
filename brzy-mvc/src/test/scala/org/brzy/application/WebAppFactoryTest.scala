package org.brzy.application

import org.brzy.config.Builder
import org.junit.Test
import org.junit.Assert._

/**
 * @author Michael Fortin
 * @version $Id : $
 */
class WebAppFactoryTest {

  @Test
  def testWebApplication = {
    val url = getClass.getClassLoader.getResource("brzy-app.b.yml")
    assertNotNull(url)
    val app = WebAppFactory.create(new Builder(url, "development").runtimeConfig)
    assertNotNull(app)
  }
}