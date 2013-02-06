package org.brzy.controller

import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers
import org.brzy.action.Path


class PathSpec extends WordSpec with ShouldMatchers with Fixtures {

  "Action path" should {
    "match" in {
      val actionPath = Path("users", "{id}/items/{iid}")
      val path = "/users/1232/items/234543"
      assert(actionPath.isMatch(path))

      val result = actionPath.parameterNames
      assert(!result.isEmpty)
      assert(2 == result.size)
      assert("id".equalsIgnoreCase( result(0)))
      assert("iid".equalsIgnoreCase( result(1)))

      val values = actionPath.extractParameterValues(path)
      assert(!values.isEmpty)
      assert(2 == values.size)
      assert("1232".equalsIgnoreCase( values(0)))
      assert("234543".equalsIgnoreCase( values(1)))
    }
    "exrtract parameters" in {
      val actionPath = Path("users", "{id}")
      val path = "/users/10"
      assert(actionPath.isMatch(path))

      val result = actionPath.parameterNames
      assert(!result.isEmpty)
      assert(1 == result.size)
      assert("id".equalsIgnoreCase( result(0)))
    }
    "parent var path" in {
      val actionPath = Path("", "users/{parent}/items")
      val path = "/users/1232/items"
      assert(actionPath.isMatch(path))

      val result = actionPath.parameterNames
      assert(!result.isEmpty)
      assert(1 == result.size)
      assert("parent".equalsIgnoreCase( result(0)))

      val values = actionPath.extractParameterValues(path)
      assert(!values.isEmpty)
      assert(1 == values.size)
      assert("1232".equalsIgnoreCase( values(0)))
    }
    "match path with pattern" in {
      val path = Path("","""users/{p:\d+}/items""")
      val target = "/users/1232/items"
      assert(path.isMatch(target))

      val result = path.parameterNames
      assert(!result.isEmpty)
      assert(1 == result.size)
      assert("p".equalsIgnoreCase( result(0)))

      val values = path.extractParameterValues(target)
      assert(!values.isEmpty)
      assert(1 == values.size)
      assert("1232".equalsIgnoreCase( values(0)))
    }
    "path doesn't match" in {
      val path = Path("", """/users/{p:\d+}/items""")
      val target = "/users/abc/items"
      assert(!path.isMatch(target))
    }
    "path with extension match" in {
      val path = Path("", """path/pixel.gif""")
      val target = "/path/pixel.gif"
      assert(path.isMatch(target))
    }
    "extract parameters base" in {
      val actionPath = Path("users", "/")
      val path = "/users"
      val result = actionPath.parameterNames
      assert(result.isEmpty)
      assert(actionPath.isMatch(path))
    }
    "extract parameters root" in {
      val actionPath = Path("", "")
      val path = "/"
      val result = actionPath.parameterNames
      assert(result.isEmpty)
      assert(actionPath.isMatch(path))
    }
  }
}