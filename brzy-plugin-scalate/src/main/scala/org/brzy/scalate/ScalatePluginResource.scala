package org.brzy.scalate

import collection.JavaConversions._
import org.brzy.plugin.{WebAppViewPlugin, ScalatePluginConfig, WebAppPlugin}

/**
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
class ScalatePluginResource(config:ScalatePluginConfig) extends WebAppViewPlugin{

  override val fileExtension = config.fileExtension

}