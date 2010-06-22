package org.brzy.config.common

import org.apache.commons.lang.builder.{CompareToBuilder, EqualsBuilder, HashCodeBuilder}
import org.slf4j.LoggerFactory

/**
 * @author Michael Fortin
 * @version $Id : $
 */
class Repository(m: Map[String, AnyRef]) extends Config(m) with Ordered[Repository] {
  val configurationName: String = "Repository"
  val id: Option[String] = m.get("id").asInstanceOf[Option[String]].orElse(None)
  val name: Option[String] = m.get("name").asInstanceOf[Option[String]].orElse(None)
  val url: Option[String] = m.get("url").asInstanceOf[Option[String]].orElse(None)
  val snapshots: Option[Boolean] = m.get("snapshots").asInstanceOf[Option[Boolean]].orElse(Option(true))
  val releases: Option[Boolean] = m.get("releases").asInstanceOf[Option[Boolean]].orElse(Option(false))

  override def asMap:Map[String,AnyRef] = m

  override def compare(that: Repository) = {
    new CompareToBuilder()
            .append(this.url.getOrElse(null), that.url.getOrElse(null))
            .toComparison
  }

  override def toString =  id.getOrElse("?") + ":" + url.getOrElse("?")

  override def equals(p1: Any) = {
    if (p1 == null)
      false
    else if(!p1.isInstanceOf[Repository])
      false
    else {
      val rhs = p1.asInstanceOf[Repository]
      new EqualsBuilder()
              .appendSuper(super.equals(p1))
              .append(url.getOrElse(null), rhs.url.getOrElse(null))
              .isEquals
    }
  }

  override def hashCode = {
    new HashCodeBuilder(23, 37)
            .append(url.getOrElse(null))
            .toHashCode
  }
}