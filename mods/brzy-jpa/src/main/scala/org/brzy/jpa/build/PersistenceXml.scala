package org.brzy.jpa.build

import org.brzy.config.common.BootConfig
import xml._
import collection.mutable.{ListBuffer, ArrayBuffer}
import transform.{RewriteRule, RuleTransformer}

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
class PersistenceXml(config:BootConfig) {
  private val template = XML.load(getClass.getClassLoader.getResource("template.persistence.xml"))
  private val children = ListBuffer[Elem]()
  private val parentName = "persistence-unit"
  
 

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