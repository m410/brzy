package org.brzy.config

import reflect.BeanProperty

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class Views extends MergeConfig[Views]{
  @BeanProperty var name:String = _
  @BeanProperty var version:String = _
  @BeanProperty var group_id:String = _

  @BeanProperty var html_version:String = _
  @BeanProperty var file_extension:String = _

  @BeanProperty var remote_location:String = _

  def +(that: Views) = {
    val view = new Views
    view.name = if(that.name != null) that.name else name
    view.version = if(that.version != null) that.version else version
    view.group_id = if(that.group_id != null) that.group_id else group_id

    view.html_version = if(that.html_version != null) that.html_version else html_version
    view.file_extension = if(that.file_extension != null) that.file_extension else file_extension
    
    view.remote_location = if(that.remote_location != null) that.remote_location else remote_location
    view
  }

//  override def toString = {
//    val newline = System.getProperty("line.separator")
//    val sb = new StringBuilder()
//    sb.append("  - application").append(newline)
//    sb.append("   - implementation").append("=").append(implementation).append(newline)
//    sb.append("   - html_version").append("=").append(html_version).append(newline)
//    sb.toString
//  }
}