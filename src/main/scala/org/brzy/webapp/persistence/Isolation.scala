package org.brzy.webapp.persistence

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
object Isolation extends Enumeration {
  type Isolation = Value

  /**
   * Use the JDBC driver's default isolation level. Kodo uses this option if you do not explicitly specify any other.
    */
  val Default         = Value("Default")

  /**
   * No transaction isolation.
   */
  val None            = Value("None")

  /**
   * Dirty reads are prevented; non-repeatable reads and phantom reads can occur.
   */
  val ReadCommited    = Value("ReadCommited")

  /**
   * Dirty reads, non-repeatable reads and phantom reads can occur.
   */
  val ReadUncommitted = Value("ReadUncommitted")

  /**
   * Dirty reads and non-repeatable reads are prevented; phantom reads can occur.
   */
  val RepeatableRead  = Value("RepeatableRead")

  /**
   * Dirty reads, non-repeatable reads, and phantom reads are prevented.
   */
  val Serializable    = Value("Serializable")
}
