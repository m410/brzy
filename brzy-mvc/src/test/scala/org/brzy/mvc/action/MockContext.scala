package org.brzy.action

import javax.servlet.ServletContext
import javax.servlet.http.HttpServlet


trait MockContext extends HttpServlet {
  val ctx:ServletContext

  override def getServletContext():ServletContext = ctx
}