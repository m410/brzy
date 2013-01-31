package org.brzy.webapp.action

/**
 * Http Method values used in the HttpMethods Constraint.
 *
 * @author Michael Fortin
 */
object HttpMethod extends Enumeration {
  type HttpMethod = Value
  val POST = Value
  val GET = Value
  val PUT = Value
  val OPTIONS = Value
  val HEAD = Value
  val DELETE = Value
  val TRACE = Value
  val CONNECT = Value

}