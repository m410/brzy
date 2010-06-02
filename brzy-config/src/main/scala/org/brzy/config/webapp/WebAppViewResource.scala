package org.brzy.plugin

import org.brzy.config.plugin.{Plugin, PluginResource}

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */

abstract class WebAppViewResource(plugin: Plugin) extends PluginResource(plugin) {
  val fileExtension:String
}