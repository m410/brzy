package org.brzy.build

import org.brzy.config.Config
import xml.transform.RuleTransformer
import xml.{Elem, XML}
import collection.mutable.ListBuffer

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class IvySettingsXml(config:Config) {
  private val parentName = "chain"
  private val template = XML.load(getClass.getClassLoader.getResource("template.ivysettings.xml"))
  private val children = ListBuffer[Elem]()

  config.repositories.foreach( repo => {
    children += <ibiblio name={repo.name} root={repo.url} m2compatible="true" />
  })

  val body = new RuleTransformer(new AddChildrenTo(parentName, children)).transform(template).head
}
