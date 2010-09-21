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