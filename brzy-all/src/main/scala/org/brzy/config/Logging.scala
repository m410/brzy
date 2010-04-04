package org.brzy.config

import reflect.BeanProperty

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class Logging {
  @BeanProperty var provider:String = _
  @BeanProperty var appenders:Array[String] = _
  @BeanProperty var loggers:Array[String] = _
  @BeanProperty var root:Array[String] = _
  // root has a level and appender-ref's
}

class Appender {
  // name class
  // layout class
  // pattern
  // file
  // rollingPolicy
  // fileNamePattern
  
}

class Logger {
  // name & level
}