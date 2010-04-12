package org.brzy.config

import reflect.BeanProperty

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class Logging {
  @BeanProperty var provider:String = _
  @BeanProperty var appenders:Array[Appender] = _
  @BeanProperty var loggers:Array[Logger] = _
  @BeanProperty var root:Root = _
}

class Appender {
  @BeanProperty var name:String = _
  @BeanProperty var appender_class:String = _
  @BeanProperty var layout:String = _
  @BeanProperty var pattern:String = _

  @BeanProperty var file:String = _
  @BeanProperty var rolling_policy:String = _
  @BeanProperty var file_name_pattern:String = _
}

class Logger {
  @BeanProperty var name:String =_
  @BeanProperty var level:String =_
}

class Root {
  @BeanProperty var level:String =_
  @BeanProperty var ref:Array[String] =_
}