package org.brzy.config

import java.lang.String
import collection.immutable.Map

/**
 *
 * @author Michael Fortin
 * @version $Id : $
 */
abstract class Config(map: Map[String, AnyRef]) {

  val configurationName: String

  def asMap: Map[String, AnyRef]

}