package org.brzy.action

import org.junit._
import org.junit.Assert._
import org.easymock.EasyMock
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import scala.collection.immutable.SortedSet
import org.brzy.mock.{User, UserController}
import javax.persistence.{Query, EntityTransaction, EntityManager, EntityManagerFactory}
import javax.servlet.{ServletResponse, ServletRequest, RequestDispatcher}
import org.springframework.mock.web.{MockServletContext, MockRequestDispatcher, MockHttpServletResponse, MockHttpServletRequest}
import org.brzy.interceptor.ProxyFactory._
import org.brzy.persistence.scalaJpa.JpaInterceptor
import org.brzy.application.WebApp
import org.brzy.config.webapp.WebAppConfig
import org.scalatest.junit.JUnitSuite
import org.brzy.config.common.BootConfig

/**
 * @author Michael Fortin
 * @version $Id:$
 */
class ServletJpaTest extends JUnitSuite {

  val sessionFactory = EasyMock.createMock(classOf[EntityManagerFactory])
  val interceptor = new JpaInterceptor(sessionFactory)
  val controllerClass = classOf[UserController]
  val controller = make(controllerClass,interceptor)

  val app = new WebApp(new WebAppConfig(new BootConfig(Map[String,AnyRef]()),null,Nil,Nil)) {
    override val services = Array[AnyRef]()
    override val controllers = Array[AnyRef]()
    override lazy val actions = SortedSet(
      new Action("/users/", controllerClass.getMethods()(1), controller, ".jsp"),
      new Action("/users/create", controllerClass.getMethods()(5), controller, ".jsp"),
      new Action("/users/save", controllerClass.getMethods()(2), controller, ".jsp"),
      new Action("/users/{id}", controllerClass.getMethods()(0), controller, ".jsp"),
      new Action("/users/{id}/edit", controllerClass.getMethods()(7), controller, ".jsp"),
      new Action("/users/{id}/delete", controllerClass.getMethods()(3), controller, ".jsp"),
      new Action("/users/{id}/update", controllerClass.getMethods()(6), controller, ".jsp"))
  }
//	controllerClass.getMethods.foreach(println _)

  @Test
  @Ignore
  def test404 = {
    EasyMock.replay(sessionFactory)

    val request = EasyMock.createMock(classOf[HttpServletRequest])
    EasyMock.expect(request.getRequestURI).andReturn("/noUser")
    EasyMock.expect(request.getRequestURI).andReturn("/noUser")
    EasyMock.expect(request.getContextPath).andReturn("")
    EasyMock.replay(request)

    val response = EasyMock.createMock(classOf[HttpServletResponse])
    response.sendError(404)
    EasyMock.replay(response)

    val servletContext = new MockServletContext
    servletContext.setAttribute("application",app)
    val servlet = new Servlet with MockContext {
      override val ctx = servletContext
    }
    servlet.service(request, response)

    EasyMock.verify(sessionFactory)
    EasyMock.verify(request)
    EasyMock.verify(response)
  }

  @Test
  @Ignore
  def testActionGet = {

    val entityTransaction = EasyMock.createMock(classOf[EntityTransaction])
    entityTransaction.begin
    EasyMock.expect(entityTransaction.isActive).andReturn(true)
    entityTransaction.commit
    EasyMock.replay(entityTransaction)

    val entityManager = EasyMock.createMock(classOf[EntityManager])
    EasyMock.expect(entityManager.getTransaction).andReturn(entityTransaction)
    EasyMock.expect(entityManager.getTransaction).andReturn(entityTransaction)
    EasyMock.expect(entityManager.find(classOf[User], 10L)).andReturn(new User())
    EasyMock.expect(entityManager.getTransaction).andReturn(entityTransaction)
    entityManager.close
    EasyMock.replay(entityManager)

    EasyMock.expect(sessionFactory.createEntityManager).andReturn(entityManager)
    EasyMock.replay(sessionFactory)


    val request2 = new MockHttpServletRequest(new MockServletContext()) {
			override def getRequestDispatcher(path:String):RequestDispatcher = {
				new MockRequestDispatcher(path) {
					override def forward( fwdReq:ServletRequest, fwdRes:ServletResponse ):Unit = {
						assertTrue("Correct rc attribute", fwdReq.getAttribute("rc") == null)
					}
				}
			}
		}


    val request = new MockHttpServletRequest("GET", "/users/10.brzy") // should be /users/
    request.setParameter("id","10")
    assertEquals(1,request.getParameterMap.size)
    assertEquals("10",request.getParameterMap.get("id")(0))
    val response = new MockHttpServletResponse()

    val servletContext = new MockServletContext
    servletContext.setAttribute("application",app)
    val servlet = new Servlet with MockContext {
      override val ctx = servletContext
    }
    servlet.service(request, response)

    assertNotNull(request.getAttribute("user"))

    EasyMock.verify(entityTransaction)
    EasyMock.verify(entityManager)
    EasyMock.verify(sessionFactory)
  }

  @Test
  @Ignore
  def testActionList = {
    val entityTransaction = EasyMock.createMock(classOf[EntityTransaction])
    entityTransaction.begin
    EasyMock.expect(entityTransaction.isActive).andReturn(true)
    entityTransaction.commit
    EasyMock.replay(entityTransaction)

    val query = EasyMock.createMock(classOf[Query])
    EasyMock.expect(query.getResultList).andReturn(new java.util.ArrayList())
    EasyMock.replay(query)

    val entityManager = EasyMock.createMock(classOf[EntityManager])
    EasyMock.expect(entityManager.getTransaction).andReturn(entityTransaction)
    EasyMock.expect(entityManager.getTransaction).andReturn(entityTransaction)
    EasyMock.expect(entityManager.createQuery("select distinct t from org.brzy.mock.User t"))
            .andReturn(query)
    EasyMock.expect(entityManager.getTransaction).andReturn(entityTransaction)
    entityManager.close
    EasyMock.replay(entityManager)

    EasyMock.expect(sessionFactory.createEntityManager).andReturn(entityManager)
    EasyMock.replay(sessionFactory)

    val request = new MockHttpServletRequest("GET", "/users")
    val response = new MockHttpServletResponse()

    val servletContext = new MockServletContext
    servletContext.setAttribute("application",app)
    val servlet = new Servlet with MockContext {
      override val ctx = servletContext
    }
    servlet.service(request, response)

    assertNotNull(request.getAttribute("userList"))
		
    EasyMock.verify(query)
    EasyMock.verify(entityTransaction)
    EasyMock.verify(entityManager)
    EasyMock.verify(sessionFactory)
  }

  @Test
  @Ignore
  def testActionCreate = {
    val request = new MockHttpServletRequest("GET", "/users/create")
    val response = new MockHttpServletResponse()

    val servletContext = new MockServletContext
    servletContext.setAttribute("application",app)
    val servlet = new Servlet with MockContext {
      override val ctx = servletContext
    }
    servlet.service(request, response)

    assertNotNull(request.getAttribute("user"))
  }

  @Test
  @Ignore
  def testActionSave = {
	
	  val entityTransaction = EasyMock.createMock(classOf[EntityTransaction])
    entityTransaction.begin
    EasyMock.expect(entityTransaction.isActive).andReturn(true)
    entityTransaction.commit
    EasyMock.replay(entityTransaction)

    val entityManager = EasyMock.createMock(classOf[EntityManager])
    EasyMock.expect(entityManager.getTransaction).andReturn(entityTransaction)
    EasyMock.expect(entityManager.getTransaction).andReturn(entityTransaction)
		entityManager.persist(EasyMock.anyObject)
    EasyMock.expect(entityManager.getTransaction).andReturn(entityTransaction)
    entityManager.close
    EasyMock.replay(entityManager)

    EasyMock.expect(sessionFactory.createEntityManager).andReturn(entityManager)
    EasyMock.replay(sessionFactory)

    val request = new MockHttpServletRequest("POST", "/users/save")
    request.setParameter("name","John Doe")
    val response = new MockHttpServletResponse()

    val servletContext = new MockServletContext
    servletContext.setAttribute("application",app)
    val servlet = new Servlet with MockContext {
      override val ctx = servletContext
    }
    servlet.service(request, response)

    assertNotNull(response.getRedirectedUrl());
    assertNotNull(request.getSession.getAttribute("flash-message"))
    assertNotNull(request.getAttribute("user"))

    EasyMock.verify(entityManager)
    EasyMock.verify(sessionFactory)
  }

  @Test
  @Ignore
  def testActionFailedSave = {
    assertTrue(false)
	}
	
  @Test
  @Ignore
  def testActionEdit = {
	  val entityTransaction = EasyMock.createMock(classOf[EntityTransaction])
    entityTransaction.begin
    EasyMock.expect(entityTransaction.isActive).andReturn(true)
    entityTransaction.commit
    EasyMock.replay(entityTransaction)

    val entityManager = EasyMock.createMock(classOf[EntityManager])
    EasyMock.expect(entityManager.getTransaction).andReturn(entityTransaction)
    EasyMock.expect(entityManager.getTransaction).andReturn(entityTransaction)
    EasyMock.expect(entityManager.find(classOf[User], 10L)).andReturn(new User())
    EasyMock.expect(entityManager.getTransaction).andReturn(entityTransaction)
    entityManager.close
    EasyMock.replay(entityManager)

    EasyMock.expect(sessionFactory.createEntityManager).andReturn(entityManager)
    EasyMock.replay(sessionFactory)

    val request = new MockHttpServletRequest("GET", "/users/10/edit")
    request.setParameter("id","10")
    val response = new MockHttpServletResponse()

    val servletContext = new MockServletContext
    servletContext.setAttribute("application",app)
    val servlet = new Servlet with MockContext {
      override val ctx = servletContext
    }
    servlet.service(request, response)

    assertNotNull("missing user", request.getAttribute("user"))
    EasyMock.verify(entityManager)
    EasyMock.verify(sessionFactory)
  }

  @Test
  @Ignore
  def testActionUpdate = {
	  val entityTransaction = EasyMock.createMock(classOf[EntityTransaction])
    entityTransaction.begin
    EasyMock.expect(entityTransaction.isActive).andReturn(true)
    entityTransaction.commit
    EasyMock.replay(entityTransaction)

    val entityManager = EasyMock.createMock(classOf[EntityManager])
    val mockUser = new User
    mockUser.id=10
    mockUser.name="Bob Jones"
    EasyMock.expect(entityManager.find(classOf[User],10L)).andReturn(mockUser)
    EasyMock.expect(entityManager.find(classOf[User],10L)).andReturn(mockUser)
    EasyMock.expect(entityManager.find(classOf[User],10L)).andReturn(mockUser)
    EasyMock.expect(entityManager.find(classOf[User],10L)).andReturn(mockUser)
    EasyMock.expect(entityManager.getTransaction).andReturn(entityTransaction)
    EasyMock.expect(entityManager.getTransaction).andReturn(entityTransaction)
		entityManager.persist(EasyMock.anyObject)
    EasyMock.expect(entityManager.getTransaction).andReturn(entityTransaction)
    entityManager.close
    EasyMock.replay(entityManager)

    EasyMock.expect(sessionFactory.createEntityManager).andReturn(entityManager)
    EasyMock.replay(sessionFactory)

    val request = new MockHttpServletRequest("POST", "/users/10/update")
    request.setParameter("id","10")
    request.setParameter("name","John Doe")
    val response = new MockHttpServletResponse()

    val servletContext = new MockServletContext
    servletContext.setAttribute("application",app)
    val servlet = new Servlet with MockContext {
      override val ctx = servletContext
    }
    servlet.service(request, response)
    
		assertNotNull("missing redirect", response.getRedirectedUrl());
    assertNotNull("missing flash", request.getSession.getAttribute("flash-message"))
    assertNotNull("missing user", request.getAttribute("user"))
    EasyMock.verify(entityManager)
    EasyMock.verify(sessionFactory)
  }
}