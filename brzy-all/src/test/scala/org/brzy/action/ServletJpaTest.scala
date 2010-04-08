package org.brzy.action

import org.junit._
import org.junit.Assert._
import org.easymock.EasyMock._
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import org.brzy.config.Config
import scala.collection.immutable.SortedSet
import org.brzy.mock.{User, UserController}
import javax.persistence.{Query, EntityTransaction, EntityManager, EntityManagerFactory}
import javax.servlet.{ServletResponse, ServletRequest, RequestDispatcher}
import org.springframework.mock.web.{MockServletContext, MockRequestDispatcher, MockHttpServletResponse, MockHttpServletRequest}
import org.brzy.interceptor.ProxyFactory._
import org.brzy.persistence.scalaJpa.JpaInterceptor
import org.brzy.application.WebApp

/**
 * @author Michael Fortin
 * @version $Id:$
 */
class ServletJpaTest {

  val sessionFactory = createMock(classOf[EntityManagerFactory])
  val interceptor = new JpaInterceptor(sessionFactory)
  val controllerClass = classOf[UserController]
  val controller = make(controllerClass,interceptor)

  val app = new WebApp(new Config()) {
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
  def test404 = {
    replay(sessionFactory)

    val request = createMock(classOf[HttpServletRequest])
    expect(request.getRequestURI).andReturn("/noUser")
    expect(request.getRequestURI).andReturn("/noUser")
    expect(request.getContextPath).andReturn("")
    replay(request)

    val response = createMock(classOf[HttpServletResponse])
    response.sendError(404)
    replay(response)

    val servletContext = new MockServletContext
    servletContext.setAttribute("application",app)
    val servlet = new Servlet with MockContext {
      override val ctx = servletContext
    }
    servlet.service(request, response)

    verify(sessionFactory)
    verify(request)
    verify(response)
  }

  @Test
  def testActionGet = {

    val entityTransaction = createMock(classOf[EntityTransaction])
    entityTransaction.begin
    expect(entityTransaction.isActive).andReturn(true)
    entityTransaction.commit
    replay(entityTransaction)

    val entityManager = createMock(classOf[EntityManager])
    expect(entityManager.getTransaction).andReturn(entityTransaction)
    expect(entityManager.getTransaction).andReturn(entityTransaction)
    expect(entityManager.find(classOf[User], 10L)).andReturn(new User())
    expect(entityManager.getTransaction).andReturn(entityTransaction)
    entityManager.close
    replay(entityManager)

    expect(sessionFactory.createEntityManager).andReturn(entityManager)
    replay(sessionFactory)


    val request2 = new MockHttpServletRequest(new MockServletContext()) {
			override def getRequestDispatcher(path:String):RequestDispatcher = {
				new MockRequestDispatcher(path) {
					override def forward( fwdReq:ServletRequest, fwdRes:ServletResponse ):Unit = {
						assertTrue("Correct rc attribute", fwdReq.getAttribute("rc") == null)
					}
				}
			}
		}


    val request = new MockHttpServletRequest("GET", "/users/10") // should be /users/
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

    verify(entityTransaction)
    verify(entityManager)
    verify(sessionFactory)
  }

  @Test
  def testActionList = {
    val entityTransaction = createMock(classOf[EntityTransaction])
    entityTransaction.begin
    expect(entityTransaction.isActive).andReturn(true)
    entityTransaction.commit
    replay(entityTransaction)

    val query = createMock(classOf[Query])
    expect(query.getResultList).andReturn(new java.util.ArrayList())
    replay(query)

    val entityManager = createMock(classOf[EntityManager])
    expect(entityManager.getTransaction).andReturn(entityTransaction)
    expect(entityManager.getTransaction).andReturn(entityTransaction)
    expect(entityManager.createQuery("select distinct t from org.brzy.mock.User t"))
            .andReturn(query)
    expect(entityManager.getTransaction).andReturn(entityTransaction)
    entityManager.close
    replay(entityManager)

    expect(sessionFactory.createEntityManager).andReturn(entityManager)
    replay(sessionFactory)

    val request = new MockHttpServletRequest("GET", "/users")
    val response = new MockHttpServletResponse()

    val servletContext = new MockServletContext
    servletContext.setAttribute("application",app)
    val servlet = new Servlet with MockContext {
      override val ctx = servletContext
    }
    servlet.service(request, response)

    assertNotNull(request.getAttribute("userList"))
		
    verify(query)
    verify(entityTransaction)
    verify(entityManager)
    verify(sessionFactory)
  }

  @Test
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
  def testActionSave = {
	
	  val entityTransaction = createMock(classOf[EntityTransaction])
    entityTransaction.begin
    expect(entityTransaction.isActive).andReturn(true)
    entityTransaction.commit
    replay(entityTransaction)

    val entityManager = createMock(classOf[EntityManager])
    expect(entityManager.getTransaction).andReturn(entityTransaction)
    expect(entityManager.getTransaction).andReturn(entityTransaction)
		entityManager.persist(anyObject)
    expect(entityManager.getTransaction).andReturn(entityTransaction)
    entityManager.close
    replay(entityManager)

    expect(sessionFactory.createEntityManager).andReturn(entityManager)
    replay(sessionFactory)

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

    verify(entityManager)
    verify(sessionFactory)
  }

  @Test
  @Ignore
  def testActionFailedSave = {
    assertTrue(false)
	}
	
  @Test
  def testActionEdit = {
	  val entityTransaction = createMock(classOf[EntityTransaction])
    entityTransaction.begin
    expect(entityTransaction.isActive).andReturn(true)
    entityTransaction.commit
    replay(entityTransaction)

    val entityManager = createMock(classOf[EntityManager])
    expect(entityManager.getTransaction).andReturn(entityTransaction)
    expect(entityManager.getTransaction).andReturn(entityTransaction)
    expect(entityManager.find(classOf[User], 10L)).andReturn(new User())
    expect(entityManager.getTransaction).andReturn(entityTransaction)
    entityManager.close
    replay(entityManager)

    expect(sessionFactory.createEntityManager).andReturn(entityManager)
    replay(sessionFactory)

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
    verify(entityManager)
    verify(sessionFactory)
  }

  @Test
  def testActionUpdate = {
	  val entityTransaction = createMock(classOf[EntityTransaction])
    entityTransaction.begin
    expect(entityTransaction.isActive).andReturn(true)
    entityTransaction.commit
    replay(entityTransaction)

    val entityManager = createMock(classOf[EntityManager])
    val mockUser = new User
    mockUser.id=10
    mockUser.name="Bob Jones"
    expect(entityManager.find(classOf[User],10L)).andReturn(mockUser)
    expect(entityManager.find(classOf[User],10L)).andReturn(mockUser)
    expect(entityManager.find(classOf[User],10L)).andReturn(mockUser)
    expect(entityManager.find(classOf[User],10L)).andReturn(mockUser)
    expect(entityManager.getTransaction).andReturn(entityTransaction)
    expect(entityManager.getTransaction).andReturn(entityTransaction)
		entityManager.persist(anyObject)
    expect(entityManager.getTransaction).andReturn(entityTransaction)
    entityManager.close
    replay(entityManager)

    expect(sessionFactory.createEntityManager).andReturn(entityManager)
    replay(sessionFactory)

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
    verify(entityManager)
    verify(sessionFactory)
  }
}