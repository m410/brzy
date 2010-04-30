package org.brzy.build

import org.brzy.config.AppConfig
import xml.transform.RuleTransformer
import collection.mutable.ListBuffer
import xml._

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class IvyXml(config:AppConfig) {
  private val parentName = "dependencies"
  private val template = XML.load(getClass.getClassLoader.getResource("template.ivy.xml"))
  private val children = ListBuffer[Elem]()

  config.dependencies.foreach( dep => {
    children += <dependency org={dep.org()} name={dep.name()} rev={dep.rev()} conf={dep.conf()} />
  })

  val body = new RuleTransformer(new AddChildrenTo(parentName, children)).transform(template).head
}