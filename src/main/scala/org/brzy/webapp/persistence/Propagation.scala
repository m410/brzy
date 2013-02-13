package org.brzy.webapp.persistence

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
object Propagation extends Enumeration {
  type Propagation = Value
  val REQUIRED = Value("REQUIRED")
  val REQUIRES_NEW = Value("REQUIRES_NEW")
  val MANDATORY = Value("MANDATORY")
  val NESTED  = Value("NESTED")
  val NOT_SUPPORTED  = Value("NOT_SUPPORTED")
  val SUPPORTS   = Value("SUPPORTS")
}
