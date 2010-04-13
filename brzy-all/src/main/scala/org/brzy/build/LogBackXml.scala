package org.brzy.build

import xml.transform.RuleTransformer
import xml._
import org.brzy.config.{Appender, Config}
import collection.mutable.{ArrayBuffer, ListBuffer}

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class LogBackXml(config:Config) {
  private val parentName = "configuration"
  private val template = XML.load(getClass.getClassLoader.getResource("template.logback.xml"))
  private val children = ListBuffer[Elem]()

  if(config.logging.appenders != null)
    config.logging.appenders.foreach( dep => {
      children += appenders(dep)
    })

  if(config.logging.loggers != null)
    config.logging.loggers.foreach( l => {
      children += <logger name={l.name} level={l.level} />
    })

  if(config.logging.root != null)
    children += Elem(null,"root", Attribute(null,"level",config.logging.root.level,Null), TopScope,
      appenderRefs(config.logging.root.ref):_*)

// <root level={config.logging.root.level}>
//    {for(ref <- config.logging.root.appender-ref)}
//      <appender-ref name={ref} />
//  </root>
  
  val body = new RuleTransformer(new AddChildrenTo(parentName, children)).transform(template).head

  def appenders(appender:Appender):Elem = {
    val arrayBuf = ArrayBuffer[Elem]()

    if(appender.file != null)
      arrayBuf += <File>{appender.file}</File>

    if(appender.rolling_policy != null)
      arrayBuf += <rollingPolicy class={appender.rolling_policy}>
        <FileNamePattern>{appender.file_name_pattern}</FileNamePattern>
      </rollingPolicy>

    if(appender.layout != null)
      arrayBuf += <layout class={appender.layout}>
        <pattern>{appender.pattern}</pattern>
      </layout>

    Elem(null,"appender", Attribute(null,"name",appender.name,
      Attribute(null,"class",appender.appender_class,Null)), TopScope, arrayBuf.toArray:_*)
  }
  def appenderRefs(root:Array[String]):Array[Node] = {
    root.map(x => <appender-ref name={x} />).toArray
  }
}