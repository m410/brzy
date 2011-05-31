package org.brzy.webapp.action

/**
 * Http Method values used in the HttpMethods Constraint.
 * 
 * @author Michael Fortin
 */
object HttpMethod extends Enumeration {
 type HttpMethod = Value
 val Post, Get, Put, Options, Head, Delete, Trace, Connect = Value
}