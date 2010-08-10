package org.brzy.scalate

import org.brzy.config.webapp.WebAppViewResource

/**
 * 
 * @author Michael Fortin
 */
class ScalateModProvider(config:ScalateModConfig) extends WebAppViewResource(config){
  override val fileExtension = config.fileExtension.get
  override val name = config.name.get

}