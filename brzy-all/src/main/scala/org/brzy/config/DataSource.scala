package org.brzy.config

import reflect.BeanProperty

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class DataSource {
  @BeanProperty var name:String = _
  @BeanProperty var driver_class:String = _
  @BeanProperty var url:String = _
  @BeanProperty var user_name:String = _
  @BeanProperty var password:String = _
}
