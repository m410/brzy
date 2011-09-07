package org.brzy.webapp.action

/**
 * Http Method values used in the HttpMethods Constraint.
 * 
 * @author Michael Fortin
 */
object HttpMethod extends Enumeration {
 type HttpMethod = Value
 val POST, GET, PUT, OPTIONS, HEAD, DELETE, TRACE, CONNECT = Value
}