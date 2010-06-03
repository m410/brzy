package org.brzy.tomcat

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */

class CompilerException(msg: String, cause: Throwable) extends RuntimeException(msg, cause) {

  def this(msg: String) =  this (msg, null)

}