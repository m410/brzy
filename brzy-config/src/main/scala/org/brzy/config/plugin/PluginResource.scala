package org.brzy.config.plugin

import org.brzy.config.plugin.Plugin

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
class PluginResource(plugin:Plugin) {
  
  def startup:Unit = {}

  def shutdown:Unit = {}

  def services:List[AnyRef] = Nil

  def interceptors:List[Interceptor] = Nil

}