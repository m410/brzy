package org.brzy.config.common

import collection.JavaConversions._
import java.util.{List=>JList}
import org.apache.commons.lang.builder.{HashCodeBuilder, EqualsBuilder, CompareToBuilder}
import collection.mutable.{Buffer, ListBuffer}

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
  val transitive: Option[Boolean] = m.get("transitive").asInstanceOf[Option[Boolean]].orElse(Option(true))

  val excludes: Option[List[Dependency]] = m.get("excludes") match {
    case Some(s) =>
      val buffer = new ListBuffer[Dependency]()
      s.asInstanceOf[List[Map[_,_]]].foreach(exclude =>{
         buffer += new Dependency(exclude.asInstanceOf[Map[String, String]])
      })
      Option(buffer.toList)
    case _ => None
  }

  /**
   * Needed by velocity to output the the excludes in the build file template.
   */
  lazy val excludesAsJList:JList[Dependency] = excludes match {
    case Some(s) =>
      val buffer = Buffer[Dependency]()
      s.copyToBuffer(buffer)
      buffer
    case _ => new java.util.ArrayList
  }

  override def asMap: Map[String, AnyRef] = m

  override def compare(that: Dependency) = {
    new CompareToBuilder()
        .append(this.conf.getOrElse(null), that.conf.getOrElse(null))
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
          .append(conf.getOrElse(null), rhs.conf.getOrElse(null))
          .append(org.getOrElse(null), rhs.org.getOrElse(null))
          .append(name.getOrElse(null), rhs.name.getOrElse(null))
          .append(rev.getOrElse(null), rhs.rev.getOrElse(null))
          .isEquals
    }
  }

  override def hashCode = {
    new HashCodeBuilder(19, 37)
        .append(conf.getOrElse(null))
        .append(org.getOrElse(null))
        .append(name.getOrElse(null))
        .append(rev.getOrElse(null))
        .toHashCode
  }
}