package org.brzy.webapp.action

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
object HttpMethod extends Enumeration {
 type HttpMethod = Value
 val Post, Get, Put, Options, Head, Delete, Trace, Connect = Value
}