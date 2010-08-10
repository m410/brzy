package org.brzy.config.webapp

import org.brzy.config.mod.{Mod, ModProvider}

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
abstract class WebAppViewResource(module: Mod) extends ModProvider {
  val fileExtension:String
  val name = module.name.get
}