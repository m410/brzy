package org.brzy.fab.cli.mod

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
import org.apache.commons.lang.builder.{EqualsBuilder, HashCodeBuilder, CompareToBuilder}

/**
 * Super Class for Module Configurations.  This handles the default or minimal values
 * required to resolve the module and load it's implemention specific version.
 *
 * @author Michael Fortin
 */
class Mod(override val map: Map[String, AnyRef]) extends BaseConf(map) with Ordered[Mod]  {

  val name: Option[String] = map.get("name").asInstanceOf[Option[String]].orElse(None)
  val version: Option[String] = map.get("version").asInstanceOf[Option[String]].orElse(None)
  val org: Option[String] = map.get("org").asInstanceOf[Option[String]].orElse(None)

  val remoteLocation: Option[String] = map.get("remote_location").asInstanceOf[Option[String]].orElse(None)
  val localLocation: Option[String] = map.get("local_location").asInstanceOf[Option[String]].orElse(None)

  override def <<(it: BaseConf) = {
    if (it == null) {
      this
    }
    else {
      val that = it.asInstanceOf[Mod]
      new Mod(Map[String, AnyRef](
        "name" -> that.name.getOrElse(null),
        "version" -> that.version.getOrElse(this.version.getOrElse(null)),
        "org" -> that.org.getOrElse(this.org.getOrElse(null)),
        "local_location" -> that.localLocation.getOrElse(this.localLocation.getOrElse(null))
      ) ++ super.<<(that).map )
    }
  }

  override def compare(that: Mod) = {
    new CompareToBuilder()
            .append(this.name.getOrElse(null), that.name.getOrElse(null))
            .append(this.org.getOrElse(null), that.org.getOrElse(null))
            .append(this.version.getOrElse(null), that.version.getOrElse(null))
            .toComparison
  }

  override def equals(p1: Any) = {
    if (p1 == null)
      false
    else {
      val rhs = p1.asInstanceOf[Mod]
      new EqualsBuilder()
              .appendSuper(super.equals(p1))
              .append(name.getOrElse(null), rhs.name.getOrElse(null))
              .append(org.getOrElse(null), rhs.org.getOrElse(null))
              .append(version.getOrElse(null), rhs.version.getOrElse(null))
              .isEquals
    }
  }

  override def hashCode = new HashCodeBuilder(11, 37)
          .append(name.getOrElse(null))
          .append(org.getOrElse(null))
          .append(version.getOrElse(null))
          .toHashCode

  override def toString = new StringBuilder()
          .append(this.getClass.getSimpleName)
          .append("[")
          .append(name.getOrElse("?"))
          .append(", ")
          .append(org.getOrElse("?"))
          .append(", ")
          .append(version.getOrElse("?"))
          .append("]")
          .toString
}
