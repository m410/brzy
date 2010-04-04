package org.brzy.build

import xml.transform.RuleTransformer
import org.brzy.config.Config
import xml.{XML, Elem}
import collection.mutable.ListBuffer
/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */

class PersistenceXml(config:Config) {
  private val parentName = "configuration"
  private val template = XML.load(getClass.getClassLoader.getResource("template.persistence.xml"))
  private val children = ListBuffer[Elem]()

  val body = new RuleTransformer(new AddChildrenTo(parentName, children)).transform(template).head
}