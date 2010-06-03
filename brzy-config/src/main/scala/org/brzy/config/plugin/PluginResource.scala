package org.brzy.config.plugin

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
trait PluginResource {
  
  def startup:Unit = {}

  def shutdown:Unit = {}

  def services:List[AnyRef] = Nil

  def interceptors:List[Interceptor] = Nil

}