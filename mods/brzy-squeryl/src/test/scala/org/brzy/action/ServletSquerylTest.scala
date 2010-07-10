package org.brzy.action

import org.junit.Assert._
import javax.servlet.{RequestDispatcher, ServletRequest, ServletResponse}
import org.springframework.mock.web.{MockHttpServletResponse, MockHttpServletRequest, MockServletContext, MockRequestDispatcher}
import org.junit.Test
import org.junit.Ignore
import org.brzy.db.DbInit
import org.brzy.interceptor.ProxyFactory._
import org.brzy.application.WebApp
import org.brzy.config.webapp.WebAppConfig
import org.scalatest.junit.JUnitSuite
import org.brzy.config.common.BootConfig
import org.brzy.squeryl.old.SquerylInterceptor
import org.brzy.interceptor.Invoker
import org.brzy.squeryl.SquerylContextManager
import java.lang.String
import collection.immutable.{Map, SortedSet}
import org.brzy.config.mod.Mod
import java.lang.reflect.Method
import org.brzy.mock.{MockModConfig, PersonController}

/**
 * @author Michael Fortin
 * @version $Id :$
 */
class ServletSquerylTest extends JUnitSuite {
  val controllerClass = classOf[PersonController]
  val threadContext = new SquerylContextManager("org.h2.Driver", "jdbc:h2:squery-test", "sa", "")
  val controller = make(controllerClass, new Invoker(List(threadContext)))

  DbInit.init

  val config: BootConfig = new BootConfig(Map[String, AnyRef]())

  val map: Map[String, AnyRef] = Map[String, AnyRef](
    "environment" -> "developement",
    "application" -> Map("org" -> "org.brzy.nothing")
    )
  val viewPlugin = new MockModConfig(Map[String, AnyRef](
    "dependencies" -> List(),
    "resource_class" -> "org.brzy.mock.MockModResource"
    ))

  val app = new WebApp(new WebAppConfig(new BootConfig(map), viewPlugin, Nil, Nil)) {
    override val services = List[AnyRef]()
    override val controllers = List[AnyRef](controller)
    val ctlMethods: Array[Method] = controllerClass.getMethods
    override lazy val actions = SortedSet(
        new Action("persons/", ctlMethods.find(_.getName == "list").get, controller, ".jsp"),
        new Action("persons/create", ctlMethods.find(_.getName == "create").get, controller, ".jsp"),
        new Action("persons/save", ctlMethods.find(_.getName == "save").get, controller, ".jsp"),
        new Action("persons/{id}", ctlMethods.find(_.getName == "get").get, controller, ".jsp"),
        new Action("persons/{id}/edit", ctlMethods.find(_.getName == "edit").get, controller, ".jsp"),
//        new Action("persons/{id}/delete", ctlMethods.find(_.getName == "delete").get, controller, ".jsp"),
        new Action("persons/{id}/update", ctlMethods.find(_.getName == "update").get, controller, ".jsp"))
    
  }

  @Test def testActionList = {
    val context: MockServletContext = new MockServletContext()
    val request = new MockHttpServletRequest(context, "GET", "persons/") {
      override def getRequestDispatcher(path: String): RequestDispatcher = {
        new MockRequestDispatcher(path) {
          assertEquals("/person/list.jsp", path)
          override def forward(fwdReq: ServletRequest, fwdRes: ServletResponse): Unit = {}
        }
      }
    }
    val response = new MockHttpServletResponse()

    val servletContext = new MockServletContext
    servletContext.setAttribute("application", app)
    val servlet = new Servlet with MockContext {
      override val ctx = servletContext
    }
    servlet.service(request, response)

    assertTrue(response.getStatus != 404)
  }

  @Test @Ignore def testActionGet = {
    val context: MockServletContext = new MockServletContext()
    val request = new MockHttpServletRequest(context, "GET", "persons/1") {
      override def getRequestDispatcher(path: String): RequestDispatcher = {
        new MockRequestDispatcher(path) {
          assertEquals("/person/get.jsp", path)
          override def forward(fwdReq: ServletRequest, fwdRes: ServletResponse): Unit = {}
        }
      }
    }
    val response = new MockHttpServletResponse()

    val servletContext = new MockServletContext
    servletContext.setAttribute("application", app)
    val servlet = new Servlet with MockContext {
      override val ctx = servletContext
    }
    servlet.service(request, response)

    assertTrue(response.getStatus != 404)
    assertNotNull(request.getAttribute("person"))
  }

  @Test def testActionCreate = {
    val context: MockServletContext = new MockServletContext()
    val request = new MockHttpServletRequest(context, "GET", "persons/create") {
      override def getRequestDispatcher(path: String): RequestDispatcher = {
        new MockRequestDispatcher(path) {
          assertEquals("/person/create.jsp", path)
          override def forward(fwdReq: ServletRequest, fwdRes: ServletResponse): Unit = {}
        }
      }
    }
    val response = new MockHttpServletResponse()

    val servletContext = new MockServletContext
    servletContext.setAttribute("application", app)
    val servlet = new Servlet with MockContext {
      override val ctx = servletContext
    }
    servlet.service(request, response)

    assertTrue(response.getStatus != 404)
    assertNotNull(request.getAttribute("person"))
  }

  @Test def testActionSave = {
    val context: MockServletContext = new MockServletContext()
    val request = new MockHttpServletRequest(context, "POST", "persons/save") {
      override def getRequestDispatcher(path: String): RequestDispatcher = {
        new MockRequestDispatcher(path) {
          assertEquals("/person/save.jsp", path)
          override def forward(fwdReq: ServletRequest, fwdRes: ServletResponse): Unit = {}
        }
      }
    }
    request.setParameter("firstName", "one")
    request.setParameter("lastName", "two")
    val response = new MockHttpServletResponse()

    val servletContext = new MockServletContext
    servletContext.setAttribute("application", app)
    val servlet = new Servlet with MockContext {
      override val ctx = servletContext
    }
    servlet.service(request, response)

    assertTrue(response.getStatus != 404)
  }

  @Test @Ignore def testActionEdit = {
    val context: MockServletContext = new MockServletContext()
    val request = new MockHttpServletRequest(context, "GET", "persons/1/edit") {
      override def getRequestDispatcher(path: String): RequestDispatcher = {
        new MockRequestDispatcher(path) {
          assertEquals("/person/edit.jsp", path)
          override def forward(fwdReq: ServletRequest, fwdRes: ServletResponse): Unit = {}
        }
      }
    }
    val response = new MockHttpServletResponse()

    val servletContext = new MockServletContext
    servletContext.setAttribute("application", app)
    val servlet = new Servlet with MockContext {
      override val ctx = servletContext
    }
    servlet.service(request, response)

    assertTrue(response.getStatus != 404)
    assertNotNull(request.getAttribute("person"))
  }

  @Test def testActionUpdate = {
    val context: MockServletContext = new MockServletContext()
    val request = new MockHttpServletRequest(context, "POST", "persons/1/update") {
      override def getRequestDispatcher(path: String): RequestDispatcher = {
        new MockRequestDispatcher(path) {
          assertEquals("/person/update.jsp", path)
          override def forward(fwdReq: ServletRequest, fwdRes: ServletResponse): Unit = {}
        }
      }
    }
    request.setParameter("firstName", "one1")
    request.setParameter("lastName", "two1")
    request.setParameter("id", "1")
    val response = new MockHttpServletResponse()

    val servletContext = new MockServletContext
    servletContext.setAttribute("application", app)
    val servlet = new Servlet with MockContext {
      override val ctx = servletContext
    }
    servlet.service(request, response)

    assertTrue(response.getStatus != 404)
  }
}