package org.brzy.scalate

import org.brzy.config.ScalatePluginConfig
import org.brzy.config.webapp.WebAppViewResource

/**
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
class ScalatePluginResource(config:ScalatePluginConfig) extends WebAppViewResource(config){

  override val fileExtension = config.fileExtension.get

}