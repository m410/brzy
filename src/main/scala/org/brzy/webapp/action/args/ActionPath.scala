package org.brzy.webapp.action.args

/**
 * Extracts the path with extensions
 * 
 * @author Michael Fortin
 */
case class ActionPath(path:String, isServlet:Boolean , isAsync:Boolean)