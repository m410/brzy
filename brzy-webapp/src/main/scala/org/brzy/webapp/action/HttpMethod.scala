package org.brzy.webapp.action

import org.scalastuff.scalabeans.Enum

/**
 * Http Method values used in the HttpMethods Constraint.
 *
 * @author Michael Fortin
 */
class HttpMethod private(val id: Int, val name: String) {
  override def toString = name
}

object HttpMethod extends Enum[HttpMethod] {
  val POST = new HttpMethod(1, "POST")
  val GET = new HttpMethod(2, "GET")
  val PUT = new HttpMethod(3, "PUT")
  val OPTIONS = new HttpMethod(4, "OPTIONS")
  val HEAD = new HttpMethod(5, "HEAD")
  val DELETE = new HttpMethod(6, "DELETE")
  val TRACE = new HttpMethod(7, "TRACE")
  val CONNECT = new HttpMethod(8, "CONNECT")

  def withName(name:String) = values.find(_.name == name.toUpperCase.trim())
          .getOrElse(throw new UnknownHttpMethodException("Unknown Method:'"+name+"'"))
}