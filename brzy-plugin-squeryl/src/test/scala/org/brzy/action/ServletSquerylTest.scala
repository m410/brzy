package org.brzy.action

import org.junit.Assert._
import org.brzy.mock.PersonController
import javax.servlet.{RequestDispatcher, ServletRequest, ServletResponse}
import org.springframework.mock.web.{MockHttpServletResponse, MockHttpServletRequest, MockServletContext, MockRequestDispatcher}
import collection.immutable.SortedSet
import org.junit.Test
import org.junit.Ignore
import org.brzy.db.DbInit
import org.brzy.interceptor.ProxyFactory._
import org.brzy.application.WebApp
import org.brzy.config.webapp.WebAppConfig
import org.scalatest.junit.JUnitSuite
import org.brzy.config.common.BootConfig
import org.brzy.squeryl.SquerylInterceptor

/**
 * @author Michael Fortin
 * @version $Id:$
 */
class ServletSquerylTest extends JUnitSuite {
  val interceptor = new SquerylInterceptor("org.h2.Driver","jdbc:h2:test", "sa", "sa")
  val controllerClass = classOf[PersonController]
  val controller = make(controllerClass,interceptor)

  DbInit.init

  val config:BootConfig = new BootConfig(Map[String,AnyRef]())

  val app = new WebApp(new WebAppConfig(new BootConfig(Map[String,AnyRef]()),null,Nil,Nil)) {
    override val services = List[AnyRef]()
    override val controllers = List[AnyRef](controller)
    override lazy val actions = SortedSet(
      new Action("persons/", controllerClass.getMethods()(1), controller, ".jsp"),
      new Action("persons/create", controllerClass.getMethods()(3), controller, ".jsp"),
      new Action("persons/save", controllerClass.getMethods()(2), controller, ".jsp"),
      new Action("persons/{id}", controllerClass.getMethods()(0), controller, ".jsp"),
      new Action("persons/{id}/edit", controllerClass.getMethods()(5), controller, ".jsp"),
      new Action("persons/{id}/delete", controllerClass.getMethods()(6), controller, ".jsp"),
      new Action("persons/{id}/update", controllerClass.getMethods()(4), controller, ".jsp"))
  }

  @Test
  @Ignore
  def testActionList = {
    val context: MockServletContext = new MockServletContext()
    val request = new MockHttpServletRequest(context,"GET", "persons/") {
      override def getRequestDispatcher(path: String): RequestDispatcher = {
        new MockRequestDispatcher(path) {
          assertEquals("/person/list.jsp",path)
          override def forward(fwdReq: ServletRequest, fwdRes: ServletResponse): Unit = {}
        }
      }
    }
    val response = new MockHttpServletResponse()

    val servletContext = new MockServletContext
    servletContext.setAttribute("application",app)
    val servlet = new Servlet with MockContext {
      override val ctx = servletContext
    }
    servlet.service(request, response)

    assertTrue(response.getStatus != 404)
  }

  @Test
  @Ignore
  def testActionGet = {
    val context: MockServletContext = new MockServletContext()
    val request = new MockHttpServletRequest(context,"GET", "persons/1") {
      override def getRequestDispatcher(path: String): RequestDispatcher = {
        new MockRequestDispatcher(path) {
          assertEquals("/person/get.jsp",path)
          override def forward(fwdReq: ServletRequest, fwdRes: ServletResponse): Unit = {}
        }
      }
    }
    val response = new MockHttpServletResponse()

    val servletContext = new MockServletContext
    servletContext.setAttribute("application",app)
    val servlet = new Servlet with MockContext {
      override val ctx = servletContext
    }
    servlet.service(request, response)

    assertTrue(response.getStatus != 404)
    assertNotNull(request.getAttribute("person"))
  }

  @Test
  @Ignore
  def testActionCreate = {
    val context: MockServletContext = new MockServletContext()
    val request = new MockHttpServletRequest(context,"GET", "persons/create") {
      override def getRequestDispatcher(path: String): RequestDispatcher = {
        new MockRequestDispatcher(path) {
          assertEquals("/person/create.jsp",path)
          override def forward(fwdReq: ServletRequest, fwdRes: ServletResponse): Unit = {}
        }
      }
    }
    val response = new MockHttpServletResponse()

    val servletContext = new MockServletContext
    servletContext.setAttribute("application",app)
    val servlet = new Servlet with MockContext {
      override val ctx = servletContext
    }
    servlet.service(request, response)

    assertTrue(response.getStatus != 404)
    assertNotNull(request.getAttribute("person"))
  }

  @Test
  @Ignore
  def testActionSave = {
    val context: MockServletContext = new MockServletContext()
    val request = new MockHttpServletRequest(context,"POST", "persons/save") {
      override def getRequestDispatcher(path: String): RequestDispatcher = {
        new MockRequestDispatcher(path) {
          assertEquals("/person/save.jsp",path)
          override def forward(fwdReq: ServletRequest, fwdRes: ServletResponse): Unit = {}
        }
      }
    }
    request.setParameter("firstName","one")
    request.setParameter("lastName","two")
    val response = new MockHttpServletResponse()

    val servletContext = new MockServletContext
    servletContext.setAttribute("application",app)
    val servlet = new Servlet with MockContext {
      override val ctx = servletContext
    }
    servlet.service(request, response)

    assertTrue(response.getStatus != 404)
  }

  @Test
  @Ignore
  def testActionEdit = {
    val context: MockServletContext = new MockServletContext()
    val request = new MockHttpServletRequest(context,"GET", "persons/1/edit") {
      override def getRequestDispatcher(path: String): RequestDispatcher = {
        new MockRequestDispatcher(path) {
          assertEquals("/person/edit.jsp",path)
          override def forward(fwdReq: ServletRequest, fwdRes: ServletResponse): Unit = {}
        }
      }
    }
    val response = new MockHttpServletResponse()

    val servletContext = new MockServletContext
    servletContext.setAttribute("application",app)
    val servlet = new Servlet with MockContext {
      override val ctx = servletContext
    }
    servlet.service(request, response)

    assertTrue(response.getStatus != 404)
    assertNotNull(request.getAttribute("person"))
  }

  @Test
  @Ignore
  def testActionUpdate = {
    val context: MockServletContext = new MockServletContext()
    val request = new MockHttpServletRequest(context,"POST", "persons/1/update") {
      override def getRequestDispatcher(path: String): RequestDispatcher = {
        new MockRequestDispatcher(path) {
          assertEquals("/person/update.jsp",path)
          override def forward(fwdReq: ServletRequest, fwdRes: ServletResponse): Unit = {}
        }
      }
    }
    request.setParameter("firstName","one1")
    request.setParameter("lastName","two1")
    request.setParameter("id","1")
    val response = new MockHttpServletResponse()

    val servletContext = new MockServletContext
    servletContext.setAttribute("application",app)
    val servlet = new Servlet with MockContext {
      override val ctx = servletContext
    }
    servlet.service(request, response)
    
    assertTrue(response.getStatus != 404)
  }
}