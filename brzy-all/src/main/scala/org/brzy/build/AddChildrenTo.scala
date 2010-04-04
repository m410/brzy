package org.brzy.build

import xml.transform.RewriteRule
import xml.{Elem, Node}

/**
 * @author Michael Fortin
 * @version $Id: $
 */
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