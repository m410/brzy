package org.brzy.controller

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers
import org.brzy.action.Path

class PathSpec extends WordSpec with ShouldMatchers with Fixtures {

  "Action path" should {
    "match" in {
      val actionPath = Path("users", "{id}/items/{iid}")
      val path = "/users/1232/items/234543"
      assertTrue(actionPath.isMatch(path))

      val result = actionPath.parameterNames
      assertTrue(!result.isEmpty)
      assertEquals(2, result.size)
      assertEquals("id", result(0))
      assertEquals("iid", result(1))

      val values = actionPath.extractParameterValues(path)
      assertTrue(!values.isEmpty)
      assertEquals(2, values.size)
      assertEquals("1232", values(0))
      assertEquals("234543", values(1))
    }
    "exrtract parameters" in {
      val actionPath = Path("users", "{id}")
      val path = "/users/10"
      assertTrue(actionPath.isMatch(path))

      val result = actionPath.parameterNames
      assertTrue(!result.isEmpty)
      assertEquals(1, result.size)
      assertEquals("id", result(0))
    }
    "parent var path" in {
      val actionPath = Path("", "users/{parent}/items")
      val path = "/users/1232/items"
      assertTrue(actionPath.isMatch(path))

      val result = actionPath.parameterNames
      assertTrue(!result.isEmpty)
      assertEquals(1, result.size)
      assertEquals("parent", result(0))

      val values = actionPath.extractParameterValues(path)
      assertTrue(!values.isEmpty)
      assertEquals(1, values.size)
      assertEquals("1232", values(0))
    }
    "match path with pattern" in {
      val path = Path("","""users/{p:\d+}/items""")
      val target = "/users/1232/items"
      assertTrue(path.isMatch(target))

      val result = path.parameterNames
      assertTrue(!result.isEmpty)
      assertEquals(1, result.size)
      assertEquals("p", result(0))

      val values = path.extractParameterValues(target)
      assertTrue(!values.isEmpty)
      assertEquals(1, values.size)
      assertEquals("1232", values(0))
    }
    "path doesn't match" in {
      val path = Path("", """/users/{p:\d+}/items""")
      val target = "/users/abc/items"
      assertFalse(path.isMatch(target))
    }
    "path with extension match" in {
      val path = Path("", """path/pixel.gif""")
      val target = "/path/pixel.gif"
      assertTrue(path.isMatch(target))
    }
    "extract parameters base" in {
      val actionPath = Path("users", "/")
      val path = "/users"
      val result = actionPath.parameterNames
      assertTrue(result.isEmpty)
      assertTrue(actionPath.isMatch(path))
    }
    "extract parameters root" in {
      val actionPath = Path("", "")
      val path = "/"
      val result = actionPath.parameterNames
      assertTrue(result.isEmpty)
      assertTrue(actionPath.isMatch(path))
    }
  }
}