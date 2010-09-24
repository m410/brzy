package org.brzy.fab.cli.mod

import org.apache.commons.lang.builder.{CompareToBuilder, EqualsBuilder, HashCodeBuilder}

/**
 * A maven or ivy repository declared in the configuration file.
 *
 * @author Michael Fortin
 */
class Repository(val map: Map[String, AnyRef]) extends Ordered[Repository] {

  val id: Option[String] = map.get("id").asInstanceOf[Option[String]].orElse(None)
  val url: Option[String] = map.get("url").asInstanceOf[Option[String]].orElse(None)

  val mvn2: Option[Boolean] = map.get("mvn2").asInstanceOf[Option[Boolean]].orElse(Option(true))
  val snapshots: Option[Boolean] = map.get("snapshots").asInstanceOf[Option[Boolean]].orElse(Option(true))
  val releases: Option[Boolean] = map.get("releases").asInstanceOf[Option[Boolean]].orElse(Option(false))

  override def compare(that: Repository) = {
    new CompareToBuilder()
            .append(this.url.orNull, that.url.orNull)
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
              .append(url.orNull, rhs.url.orNull)
              .isEquals
    }
  }

  override def hashCode = {
    new HashCodeBuilder(23, 37)
            .append(url.orNull)
            .toHashCode
  }
}