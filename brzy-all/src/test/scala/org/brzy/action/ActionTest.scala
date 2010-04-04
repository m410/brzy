package org.brzy.action

import args.Parameters
import org.springframework.mock.web.MockHttpServletRequest
import org.junit.Test
import org.junit.Assert._
import collection.immutable.SortedSet
import org.brzy.mock.UserController
import javax.servlet.http.HttpServletRequest
import org.easymock.EasyMock._

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class ActionTest {

  @Test
  def testCompare = {
    val ctlr = new UserController()
    val clazz = ctlr.getClass
    val method = clazz.getMethods()(0)
    val action: Action = new Action("users/", method, ctlr)
    val action1: Action = new Action("users/{id}/companies/{cid}", method, ctlr)
    val action2: Action = new Action("users/save", method, ctlr)
    val action3: Action = new Action("users/create", method, ctlr)
    val action4: Action = new Action("users/{id}/delete", method, ctlr)
    val action5: Action = new Action("users/{id}/update", method, ctlr)
    val action6: Action = new Action("users/{id}/edit", method, ctlr)
    val action7: Action = new Action("users/{id}", method, ctlr)
    val actions = SortedSet(action, action1, action2, action3, action4, action5, action6, action7)

    val array = new Array[Action](8)
    actions.copyToArray(array)
    assertEquals(8, array.size)
    assertEquals(action, array(0))  // users
    assertEquals(action3, array(1)) // users/create
    assertEquals(action2, array(2)) // users/save
    assertEquals(action7, array(3)) // users/id
    assertEquals(action1, array(4)) // users/id/companies/id
    assertEquals(action4, array(5)) // users/id/delete
    assertEquals(action6, array(6)) // users/id/edit
    assertEquals(action5, array(7)) // users/id/update
  }

  @Test
  def testDefaultView = {
    val ctlr = new UserController()
    val clazz = ctlr.getClass
    val method = clazz.getMethods()(1)
    val action: Action = new Action("users/", method, ctlr)
    assertEquals("/user/list.ssp",action.defaultView)
  }

  @Test
  def testMatchPath = {
    val ctlr = new UserController()
    val clazz = ctlr.getClass
    val method = clazz.getMethods()(0)
    val action: Action = new Action("users/{id}/items/{item}", method, ctlr)

    val path = "users/1232/items/234543"
    val result = action.pattern.findFirstIn(path)
    assertTrue(!result.isEmpty)
    assertEquals(path, result.get)

    val path2 = "users/"
    val result2 = action.pattern.findFirstIn(path2)
    assertTrue(result2.isEmpty)
  }

  @Test
  def testParameterExtract = {
    val ctlr = new UserController()
    val clazz = ctlr.getClass
    val method = clazz.getMethods()(0)
    val action: Action = new Action("users/{id}/items/{item}", method, ctlr)

    val path = "users/1232/items/234543"
    val result = action.matchParameters(path)
    assertNotNull(result)
    assertEquals(2, result.size)
    assertEquals("1232", result(0))
    assertEquals("234543", result(1))
  }

  @Test
  def testParameterMapExtraction = {
    val ctlr = new UserController()
    val clazz = ctlr.getClass
    val method = clazz.getMethods()(0)
    val action: Action = new Action("users/{id}/items/{item}", method, ctlr)

    val path = "users/1232/items/234543"
    val result = action.matchParameterIds
    assertNotNull(result)
    assertEquals(2, result.size)
    assertEquals("id", result(0))
    assertEquals("item", result(1))
  }

}