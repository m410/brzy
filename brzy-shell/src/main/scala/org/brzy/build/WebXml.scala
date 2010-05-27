package org.brzy.build

import collection.mutable.ListBuffer
import xml.transform.RuleTransformer
import org.brzy.config.WebappConfig
import xml._
import collection.JavaConversions._

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
class WebXml(config:WebappConfig) {
  private val parentName = "web-app"
  private val template = XML.load(getClass.getClassLoader.getResource("template.web.xml"))
  private val children = ListBuffer[Elem]()

  for(row <- config.webXml.get) {
    val keyvals = row.asInstanceOf[Map[String,AnyRef]].map(nvp => nvp)
    keyvals.foreach(keyVal => {
        if(keyVal._2.isInstanceOf[java.lang.String]) {
          val value = keyVal._2.asInstanceOf[String]
          children += Elem(null,keyVal._1, null, TopScope, new Text(value))
        }
        else {
          val map = keyVal._2.asInstanceOf[Map[String,String]]
          children += Elem(null,keyVal._1, null, TopScope, makeChildren(map):_*)
        }
      }
    )
  }

  val body = new RuleTransformer(new AddChildrenTo(parentName, children)).transform(template).head

  def makeChildren(children:Map[String,String]):Array[Node] = {

    if(children == null || children.size == 0)
      Array(Text(""))
    else {
      val nodes = children.map(f=>
        if(f._2 != null)
          Elem(null,f._1, null, TopScope, new Text(f._2))
        else
          Elem(null,f._1, null, TopScope, new Text(""))
      )
			println("nodes=" + nodes)
      nodes.toArray
    }
  }
}