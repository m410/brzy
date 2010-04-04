package org.brzy.build

import org.brzy.config.Config
import xml.{XML, Elem}
import collection.mutable.ListBuffer
import xml.transform.RuleTransformer

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class LogBackXml(config:Config) {
  private val parentName = "configuration"
  private val template = XML.load(getClass.getClassLoader.getResource("template.logback.xml"))
  private val children = ListBuffer[Elem]()

//  config.dependencies.foreach( dep => {
//    children += <dependency org={dep.org()} name={dep.name()} rev={dep.rev()} conf={dep.conf()} />
//  })

  val body = new RuleTransformer(new AddChildrenTo(parentName, children)).transform(template).head
}