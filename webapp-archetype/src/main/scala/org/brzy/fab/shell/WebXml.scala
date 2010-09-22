/*
 * Copyright 2010 Michael Fortin <mike@brzy.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
/*
 * Copyright 2010 Michael Fortin <mike@brzy.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.brzy.fab.shell

import xml._
import org.brzy.application.WebAppConf

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
 */
class WebXml(config: WebAppConf) {
  val content =
  <web-app xmlns="http://java.sun.com/xml/ns/javaee"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
           version="3.0">
    {config.webXml.map(row => {
    val keyvals = row.asInstanceOf[Map[String, AnyRef]].map(nvp => nvp)
    keyvals.map(keyVal => {
      if (keyVal._2.isInstanceOf[java.lang.String]) {
        Elem(null, keyVal._1, null, TopScope, new Text(keyVal._2.asInstanceOf[String]))
      }
      else {
        Elem(null, keyVal._1, null, TopScope, makeChildren(keyVal._2.asInstanceOf[Map[String, String]]): _*)
      }
    })
    })}
  </web-app>


  def makeChildren(children: Map[String, String]): Array[Node] = {
    if (children == null || children.size == 0)
      Array(Text(""))
    else {
      val nodes = children.map(f =>
        if (f._2 != null)
          Elem(null, f._1, null, TopScope, new Text(f._2))
        else
          Elem(null, f._1, null, TopScope, new Text(""))
        )
      nodes.toArray
    }
  }

  def saveToFile(path: String) = XML.save(path, content, "UTF-8", true, null)

}