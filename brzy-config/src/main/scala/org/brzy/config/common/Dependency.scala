package org.brzy.config.common

import collection.mutable.ListBuffer
import org.apache.commons.lang.builder.{HashCodeBuilder, EqualsBuilder, CompareToBuilder}

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
    case s: Some[List[Dependency]] =>
      val buffer = new ListBuffer[Dependency]()
      s.get.foreach(exclude => buffer += new Dependency(exclude.asInstanceOf[Map[String, String]]))
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
          case s: Some[List[Dependency]] => s.get.map(_.asMap).toList
          case _ => null
        }
      })
  }

  override def compare(that: Dependency) = {
    new CompareToBuilder()
            .append(this.org.get, that.org.get)
            .append(this.name.get, that.name.get)
            .append(this.rev.get, that.rev.get)
            .toComparison
  }

  override def toString = org.get + ":" + name.get + ":" + rev.get

  override def equals(p1: Any) = {
    if (p1 == null)
      false
    else {
      val rhs = p1.asInstanceOf[Dependency]
      new EqualsBuilder()
              .appendSuper(super.equals(p1))
              .append(org.get, rhs.org.get)
              .append(name.get, rhs.name.get)
              .append(rev.get, rhs.rev.get)
              .isEquals
    }
  }

  override def hashCode = {
    new HashCodeBuilder(19, 37)
            .append(org.get)
            .append(name.get)
            .append(rev.get)
            .toHashCode
  }
}