package org.brzy.build

import collection.mutable.ListBuffer
import xml.transform.RuleTransformer
import org.brzy.config.{WebXmlNode, Config}
import xml._

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
    if(node.content != null)
      children += Elem(null,node.name, null, TopScope, new Text(node.content))
    else if(node.children != null && node.children.length >0)
      children += Elem(null,node.name, null, TopScope, makeChildren(node.children):_*)
    else
      children += Elem(null,node.name, null, TopScope, new Text(""))
  })

  val body = new RuleTransformer(new AddChildrenTo(parentName, children)).transform(template).head

  def makeChildren(children:Array[WebXmlNode]):Array[Node] = {

    if(children == null || children.length== 0)
      Array(Text(""))
    else {
      val nodes = children.map(f=>
        if(f.content != null)
          Elem(null,f.name, null, TopScope, new Text(f.content))
        else
          Elem(null,f.name, null, TopScope, new Text(""))
      )
      println(nodes)
      nodes.toArray
    }
  }
}