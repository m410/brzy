package org.brzy.config.webapp

import org.brzy.config.mod.{Mod, ModResource}

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
abstract class WebAppViewResource(plugin: Mod) extends ModResource {
  val fileExtension:String
}