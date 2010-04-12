package org.brzy.config

import org.junit.Test
import org.junit.Assert._

/**
 *
 * @author Michael Fortin
 * @version $Id: $
 */
class BuilderTest {

  @Test
  def testCreateBuilder = {
    val url = getClass.getClassLoader.getResource("brzy-app.b.yml")
    assertNotNull(url)
    val app = new Builder(url,"development").config
    assertNotNull(app)
    assertNotNull(app.version)
    assertNotNull(app.name)
    assertEquals("Test app", app.name)
    assertNotNull(app.author)
    assertNotNull(app.description)
    assertNotNull(app.group_id)
    assertNotNull(app.artifact_id)
    assertNotNull(app.webapp_context)
    assertNotNull(app.db_migration)
    assertNotNull(app.src_package )
    assertNotNull(app.data_source)
    assertEquals("something",app.data_source.name)
    assertNotNull(app.dependencies)
//    assertNotNull("org.apache.wicket:wicket:jar:1.4-rc6",app.dependencies(0).compile)
    assertNotNull(app.logging)
//    assertNotNull(app.plugins)
    assertNotNull(app.web_xml)
    assertNotNull(app.environment_overrides)
    assertEquals(3,app.environment_overrides.size)
    assertEquals(false,app.environment_overrides(0).db_migration)
//    assertEquals(4,app.plugins.size)
//    assertNotNull(app.application_properties)
//    assertEquals(1,app.application_properties.size)
  }
}