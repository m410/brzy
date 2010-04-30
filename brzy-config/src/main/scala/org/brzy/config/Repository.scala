package org.brzy.config

import reflect.BeanProperty

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class Repository {
  @BeanProperty var id: String = _
  @BeanProperty var name: String = _
  @BeanProperty var url: String = _
  @BeanProperty var snapshots: Boolean = false
  @BeanProperty var releases: Boolean = true


  override def toString = new StringBuilder()
    .append("Repository - id: ").append(id)
    .toString
}