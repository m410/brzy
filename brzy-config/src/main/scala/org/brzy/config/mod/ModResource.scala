package org.brzy.config.mod

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
trait ModResource {

  val name:String

  def startup:Unit = {}

  def shutdown:Unit = {}

  def services:List[AnyRef] = Nil
}