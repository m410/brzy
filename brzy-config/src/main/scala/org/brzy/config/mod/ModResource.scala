package org.brzy.config.mod

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
trait ModResource {

  val name:String

  def startup:Unit = {}

  def shutdown:Unit = {}

  val serviceMap:Map[String,AnyRef] = Map()
}