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
package org.brzy.fab.conf

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
          .append(name.getOrElse("?"))
          .append(", ")
          .append(org.getOrElse("?"))
          .append(", ")
          .append(version.getOrElse("?"))
          .toString
}

object Mod {
  def apply(map: Map[String, AnyRef]) = map.get("mod_type") match {
    case Some("view") => new ViewMod(map)
    case Some("persistence") => new PersistenceMod(map)
    case Some("runtime") => new RuntimeMod(map)
    case Some("build") => new BuildMod(map)
    case _ => new Mod(map)
  }
}