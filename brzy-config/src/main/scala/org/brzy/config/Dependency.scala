package org.brzy.config

import reflect.BeanProperty

/**
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
class Dependency {
  @BeanProperty var org:String =_
  @BeanProperty var name:String =_
  @BeanProperty var rev:String =_
  @BeanProperty var conf:String =_

}