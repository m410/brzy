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
package org.brzy.shell

import xml.transform.RuleTransformer
import xml._
import java.lang.String
import org.brzy.config.common.{Appender, BootConfig}
import collection.mutable.{ArrayBuffer, ListBuffer}

/**
 * @author Michael Fortin
 */
class LogBackXml(config:BootConfig) {
  private val parentName = "configuration"
  private val template = XML.load(getClass.getClassLoader.getResource("template.logback.xml"))
  private val children = ListBuffer[Elem]()

  if(config.logging.get.appenders.isDefined)
    config.logging.get.appenders.get.foreach( dep => {
      children += appenders(dep)
    })

  if(config.logging.get.loggers.isDefined)
    config.logging.get.loggers.get.foreach( l => {
      children += <logger name={l.name.get} level={l.level.get} />
    })

  if(config.logging.get.root.isDefined)
    children += {
      val level: String = config.logging.get.root.get.level.get
      val refs: List[String] = config.logging.get.root.get.ref.get
      Elem(null,"root", Attribute(null,"level",level,Null), TopScope, appenderRefs(refs):_*)
    }

  val body = new RuleTransformer(new AddChildrenTo(parentName, children)).transform(template).head

  def appenders(appender:Appender):Elem = {
    val arrayBuf = ArrayBuffer[Elem]()

    if(appender.file.isDefined)
      arrayBuf += <File>{appender.file.get}</File>

    if(appender.rollingPolicy.isDefined)
      arrayBuf += <rollingPolicy class={appender.rollingPolicy.get}>
        <FileNamePattern>{appender.fileNamePattern.get}</FileNamePattern>
      </rollingPolicy>

    if(appender.layout != null)
      arrayBuf += <encoder class={appender.layout.get}>
        <pattern>{appender.pattern.get}</pattern>
      </encoder>

    val name: String = appender.name.get
    val apClass: String = appender.appenderClass.get
    val attr: Attribute = Attribute(null, "class", apClass, Null)
    Elem(null,"appender", Attribute(null,"name",name, attr), TopScope, arrayBuf.toArray:_*)
  }
  def appenderRefs(root:Seq[String]):Array[Node] = {
    if(root != null)
      root.map(x => <appender-ref ref={x} />).toArray
    else
      Array.empty
  }
}