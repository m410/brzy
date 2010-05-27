package org.brzy.build

import org.brzy.config.WebappConfig
import xml.transform.RuleTransformer
import xml.{Elem, XML}
import collection.mutable.ListBuffer

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class IvySettingsXml(config:WebappConfig) {
  private val parentName = "chain"
  private val template = XML.load(getClass.getClassLoader.getResource("template.ivysettings.xml"))
  private val children = ListBuffer[Elem]()

  config.repositories.get.foreach( repo => {
    children += <ibiblio name={repo.id.get} root={repo.url.get} m2compatible="true" />
  })

  val body = new RuleTransformer(new AddChildrenTo(parentName, children)).transform(template).head
}
