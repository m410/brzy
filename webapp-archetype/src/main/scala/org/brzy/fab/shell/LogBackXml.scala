/*
 * Copyright 2010 Michael Fortin <mike@brzy.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
/*
 * Copyright 2010 Michael Fortin <mike@brzy.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.brzy.fab.shell


import xml._
import java.lang.String
import org.brzy.application.WebAppConf

/**
 * Create the logback.xml configuration file
 * @author Michael Fortin
 */
class LogBackXml(config: WebAppConf) {
  
  val content =
<configuration>
  {if (config.logging.appenders.isDefined) config.logging.appenders.get.map(dep => {
  <appender name={dep.name.get} class={dep.appenderClass.get}>
    {if (dep.file.isDefined) <File>{dep.file.get} </File>}
    {if (dep.rollingPolicy.isDefined)
    <rollingPolicy class={dep.rollingPolicy.get}>
      <FileNamePattern> {dep.fileNamePattern.get} </FileNamePattern>
    </rollingPolicy>}
    {if (dep.layout != null)
    <encoder class={dep.layout.get}>
      <pattern> {dep.pattern.get} </pattern>
    </encoder>}
  </appender>
  })}
  {if (config.logging.loggers.isDefined) config.logging.loggers.get.map(l => {
  <logger name={l.name.get} level={l.level.get}/>
  })}
  <root>
    <level value={config.logging.root.get.level.get}/>
    {config.logging.root.get.ref.get.map(x => {
    <appender-ref ref={x}/>
  })}
  </root>
</configuration>

  def saveToFile(path:String) = XML.save(path, content, "UTF-8", true, null)

}