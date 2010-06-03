package org.brzy.view

import ViewFunctions._
import org.junit.Test
import org.junit.Assert._
import javax.servlet.ServletResponse
import org.springframework.mock.web.{MockServletContext, MockHttpServletRequest}
import org.scalatest.junit.JUnitSuite

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class ViewFunctionTest extends JUnitSuite {

  @Test
  def testResource = {
    val request = new MockHttpServletRequest("GET","/resource")
    request.setContextPath("/context")
    assertEquals("/context", request.getContextPath)
    assertEquals("/context/resource",resource("/resource",request))
  }
}