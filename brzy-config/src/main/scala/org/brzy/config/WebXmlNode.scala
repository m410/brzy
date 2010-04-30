package org.brzy.config

import reflect.BeanProperty

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class WebXmlNode {
  @BeanProperty var name:String = _
  @BeanProperty var content:String = _
  @BeanProperty var children:Array[WebXmlNode] = _

  override def toString = new StringBuilder()
    .append("name: ").append(name)
    .toString
}