package org.brzy.config

import reflect.BeanProperty

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class Plugin {
  @BeanProperty var name:String = _
  @BeanProperty var implementation:String = _
  @BeanProperty var version:String = _
  @BeanProperty var scan_package:String = _
  @BeanProperty var properties:java.util.HashMap[String,String] =_
}