package org.brzy.build

import xml.{XML, Elem}
import collection.mutable.ListBuffer
import xml.transform.RuleTransformer
import org.brzy.config.{WebXmlNode, Config}

/**
 * context-param
 * description
 * display-name
 * distributable
 * ejb-ref
 * ejb-local-ref
 * env-entry
 * error-page
 * filter
 * filter-mapping
 * icon
 * listener
 * login-config
 * mime-mapping
 * resource-env-ref
 * resource-ref
 * security-constraint
 * security-role
 * servlet
 * servlet-mapping
 * session-config
 * welcome-file-list
 *
 * @author Michael Fortin
 * @version $Id: $
 */
class WebXml(config:Config) {
  private val parentName = "web-app"
  private val template = XML.load(getClass.getClassLoader.getResource("template.web.xml"))
  private val children = ListBuffer[Elem]()

  config.web_xml.foreach( node => {
    children += <test /> //<{node.name}>{node.content}</{node.name}>
  })

  val body = new RuleTransformer(new AddChildrenTo(parentName, children)).transform(template).head

  def appendNode(elem:Elem, node:WebXmlNode) {
    
  }
}