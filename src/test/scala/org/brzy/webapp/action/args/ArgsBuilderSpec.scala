package org.brzy.webapp.action.args

import org.scalatest.{WordSpec, FlatSpec}
import org.springframework.mock.web.{MockServletContext, MockHttpServletRequest}
import org.scalatest.matchers.ShouldMatchers


class ArgsBuilderSpec extends WordSpec with ShouldMatchers with Fixtures {

  "A ArgsBuilder" should {
    "create parameters" in {
      val request = new MockHttpServletRequest(new MockServletContext())
      val args = ArgsBuilder(request,controller.actions(0))
      assert(args.length == 1)
      assert(args(0).isInstanceOf[Parameters])
    }

    "create parameters and pricipal" in  {
      val request = new MockHttpServletRequest(new MockServletContext())
      val args = ArgsBuilder(request,controller.actions(1))
      assert(args.length == 2)
      assert(args(0).isInstanceOf[Parameters])
      assert(args(1).isInstanceOf[Principal])
    }
    "pull action path with extension" in {
      val apath = ArgsBuilder.parseActionPath("/ctx/usr/path.brzy","/ctx")
      apath.isAsync should be (false)
      apath.isServlet should be (true)
      apath.path should be equals("/usr/path")
    }
    "pull action path without extension" in {
      val apath = ArgsBuilder.parseActionPath("/usr/path","")
      apath.isAsync should be (false)
      apath.isServlet should be (false)
      apath.path should be equals("/usr/path")
    }
    "pull action path with extra slashes" in {
      val apath = ArgsBuilder.parseActionPath("/ctx////usr/path.brzy","/ctx")
      apath.isAsync should be (false)
      apath.isServlet should be (true)
      apath.path should be equals("/usr/path")
    }
    "pull action path with async extension" in {
      val apath = ArgsBuilder.parseActionPath("/usr/path.brzy_async","")
      apath.isAsync should be (true)
      apath.isServlet should be (true)
      apath.path should be equals("/usr/path")
    }
  }
}