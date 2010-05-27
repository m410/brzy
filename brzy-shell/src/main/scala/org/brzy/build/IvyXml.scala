package org.brzy.build

import org.brzy.config.WebappConfig
import xml.transform.RuleTransformer
import collection.mutable.ListBuffer
import xml._

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class IvyXml(config:WebappConfig) {
  private val parentName = "dependencies"
  private val template = XML.load(getClass.getClassLoader.getResource("template.ivy.xml"))
  private val children = ListBuffer[Elem]()

  config.dependencies.get.foreach( dep => {
    children += <dependency org={dep.org.get} name={dep.name.get} rev={dep.rev.get} conf={dep.conf.get} />
  })

  val body = new RuleTransformer(new AddChildrenTo(parentName, children)).transform(template).head
}