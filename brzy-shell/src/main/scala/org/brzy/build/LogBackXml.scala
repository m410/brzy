package org.brzy.build

import xml.transform.RuleTransformer
import xml._
import org.brzy.config.{Appender, WebappConfig}
import collection.mutable.{ArrayBuffer, ListBuffer}

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class LogBackXml(config:WebappConfig) {
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

    if(appender.rollingPolicy != null)
      arrayBuf += <rollingPolicy class={appender.rollingPolicy}>
        <FileNamePattern>{appender.fileNamePattern}</FileNamePattern>
      </rollingPolicy>

    if(appender.layout != null)
      arrayBuf += <encoder class={appender.layout}>
        <pattern>{appender.pattern}</pattern>
      </encoder>

//    <appender name={appender.name} class={appender.appender_class}>
//      {arrayBuf.toArray}
//    </appender>
    Elem(null,"appender", Attribute(null,"name",appender.name,
      Attribute(null,"class",appender.appenderClass,Null)), TopScope, arrayBuf.toArray:_*)
  }
  def appenderRefs(root:Seq[String]):Array[Node] = {
    if(root != null)
      root.map(x => <appender-ref ref={x} />).toArray
    else
      Array.empty
  }
}