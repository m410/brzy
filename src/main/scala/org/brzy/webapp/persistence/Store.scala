package org.brzy.webapp.persistence

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
trait Store[PK,E] {

  def make(map: Map[String, AnyRef])(implicit m: Manifest[E]): E

  /**
   * This creates and instance of the entity using a default no-args constructor.
   */
  def blankInstance: E

  def apply(id:PK):E

  def get(id:PK):Option[E]

  def list(size: Int = 50, offset: Int = 0):List[E]
}
