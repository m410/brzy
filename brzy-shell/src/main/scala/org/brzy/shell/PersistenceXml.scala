package org.brzy.shell

import org.brzy.config.common.BootConfig
import xml._
import collection.mutable.{ListBuffer, ArrayBuffer}
import transform.{RewriteRule, RuleTransformer}

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
class PersistenceXml(config:BootConfig) {
  private val template = XML.load(getClass.getClassLoader.getResource("template.persistence.xml"))
  private val children = ListBuffer[Elem]()
  private val parentName = "persistence-unit"

  // TODO Add children, classes and properties from the configuration

  val body = new RuleTransformer(new AddChildrenTo(parentName, children)).transform(template).head

}
