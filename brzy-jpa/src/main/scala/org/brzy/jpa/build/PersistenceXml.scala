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
package org.brzy.jpa.build

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

class AddChildrenTo(label: String, newChildren: Seq[Node]) extends RewriteRule {

  override def transform(n: Node) = n match {
    case n @ Elem(_, `label`, _, _, _*) => addChild(n, newChildren)
    case other => other
  }

  def addChild(n: Node, children: Seq[Node]) = n match {
    case Elem(prefix, label, attributes, scope, child @ _*) =>
      Elem(prefix, label, attributes, scope, child ++ children : _*)
    case _ => error("Can only add children to elements!")
  }
}