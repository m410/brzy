package org.brzy.bean

import java.beans.ConstructorProperties

/**
 * Helper class that constructs instances of scala classes using the ConstructorProperties
 * annotation java bean annotation.
 *
 * @see java.bean.ConstuctorProperties
 * @author Michael Fortin
 */
object Construct {

  /**
   * If there is more than one constructor this looks for the one with the most
   * arguments and uses it.  This uses the Name annotation to identify the
   * constructor args.
   */
  def apply[T](map: Map[String, Any])(implicit m: Manifest[T]): T = {
    val c = m.erasure
    val argNames = c.getAnnotation(classOf[ConstructorProperties]).value.asInstanceOf[Array[String]]
    val constructor = c.getConstructors.find(c => c.getParameterTypes.size > 0).get
    val args = argNames.map(name => map.get(name).get).asInstanceOf[Array[_ <: Object]]
    constructor.newInstance(args: _*).asInstanceOf[T]
  }
}