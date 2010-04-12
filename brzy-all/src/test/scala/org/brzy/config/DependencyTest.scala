package org.brzy.config

import org.junit.Test
import org.junit.Assert._

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class DependencyTest {

  @Test
  def testDependency = {
    val dependency = new Dependency()
    dependency.lib = "compile:org.somecompany:app:1.0.0"
    assertEquals("org.somecompany",dependency.org)
    assertEquals("app",dependency.name)
    assertEquals("1.0.0",dependency.rev)
    assertEquals("compile",dependency.conf)
  }
}