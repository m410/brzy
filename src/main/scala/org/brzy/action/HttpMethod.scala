package org.brzy.action

/**
 * Http Method values used in the HttpMethods Constraint.
 *
 * @author Michael Fortin
 */
object HttpMethod extends Enumeration {
  type HttpMethod = Value
  val POST = Value("POST")
  val GET = Value("GET")
  val PUT = Value("PUT")
  val OPTIONS = Value("OPTIONS")
  val HEAD = Value("HEAD")
  val DELETE = Value("DELETE")
  val TRACE = Value("TRACE")
  val CONNECT = Value("CONNECT")

}