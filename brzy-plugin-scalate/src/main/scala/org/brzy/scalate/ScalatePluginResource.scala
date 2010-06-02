package org.brzy.scalate

import org.brzy.plugin.{WebAppViewResource, ScalatePluginConfig}

/**
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
class ScalatePluginResource(config:ScalatePluginConfig) extends WebAppViewResource(config){

  override val fileExtension = config.fileExtension.get

}