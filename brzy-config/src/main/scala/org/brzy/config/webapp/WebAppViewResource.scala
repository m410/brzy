package org.brzy.config.webapp

import org.brzy.config.plugin.{Plugin, PluginResource}

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
abstract class WebAppViewResource(plugin: Plugin) extends PluginResource {
  val fileExtension:String
}