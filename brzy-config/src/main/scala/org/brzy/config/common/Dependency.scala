package org.brzy.config.common

import collection.mutable.ListBuffer
import org.apache.commons.lang.builder.{HashCodeBuilder, EqualsBuilder, CompareToBuilder}
import org.slf4j.LoggerFactory

/**
 * @author Michael Fortin
 * @version $Id : $
 */
class Dependency(m: Map[String, AnyRef]) extends Config(m) with Ordered[Dependency] {
  val configurationName: String = "Dependency"
  val org: Option[String] = m.get("org").asInstanceOf[Option[String]].orElse(None)
  val name: Option[String] = m.get("name").asInstanceOf[Option[String]].orElse(None)
  val rev: Option[String] = m.get("rev").asInstanceOf[Option[String]].orElse(None)
  val conf: Option[String] = m.get("conf").asInstanceOf[Option[String]].orElse(None)
  val transitive: Boolean = true

  val excludes: Option[List[Dependency]] = m.get("excludes") match {
    case Some(s) =>
      val buffer = new ListBuffer[Dependency]()
      s.asInstanceOf[List[Dependency]].foreach(exclude => buffer += new Dependency(exclude.asInstanceOf[Map[String, String]]))
      Option(buffer.toList)
    case _ => None
  }

  override def asMap: Map[String, AnyRef] = {
    Map[String, AnyRef](
      "org" -> org.getOrElse(null),
      "name" -> name.getOrElse(null),
      "rev" -> rev.getOrElse(null),
      "conf" -> conf.getOrElse(null),
      "exculdes" -> {
        excludes match {
          case Some(a) => a.asInstanceOf[List[Dependency]].map(_.asMap).toList
          case _ => null
        }
      })
  }

  override def compare(that: Dependency) = {
    new CompareToBuilder()
            .append(this.org.getOrElse(null), that.org.getOrElse(null))
            .append(this.name.getOrElse(null), that.name.getOrElse(null))
            .append(this.rev.getOrElse(null), that.rev.getOrElse(null))
            .toComparison
  }

  override def toString = org.getOrElse("?") + ":" + name.getOrElse("?") + ":" + rev.getOrElse("?")

  override def equals(p1: Any) = {
    if (p1 == null)
      false
    else if(!p1.isInstanceOf[Dependency])
      false
    else {
      val rhs = p1.asInstanceOf[Dependency]
      new EqualsBuilder()
              .appendSuper(super.equals(p1))
              .append(org.getOrElse(null), rhs.org.getOrElse(null))
              .append(name.getOrElse(null), rhs.name.getOrElse(null))
              .append(rev.getOrElse(null), rhs.rev.getOrElse(null))
              .isEquals
    }
  }

  override def hashCode = {
    new HashCodeBuilder(19, 37)
            .append(org.getOrElse(null))
            .append(name.getOrElse(null))
            .append(rev.getOrElse(null))
            .toHashCode
  }
}