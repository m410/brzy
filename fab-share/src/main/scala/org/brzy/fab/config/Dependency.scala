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
package org.brzy.fab.config


import org.apache.commons.lang.builder.{CompareToBuilder, HashCodeBuilder, EqualsBuilder}
import collection.mutable.ListBuffer

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
class Dependency(val map: Map[String, AnyRef]) extends Ordered[Dependency] {
  val configurationName: String = "Dependency"
  val org: Option[String] = map.get("org").asInstanceOf[Option[String]].orElse(None)
  val name: Option[String] = map.get("name").asInstanceOf[Option[String]].orElse(None)
  val rev: Option[String] = map.get("rev").asInstanceOf[Option[String]].orElse(None)
  val conf: Option[String] = map.get("conf").asInstanceOf[Option[String]].orElse(None)
  val transitive: Option[Boolean] = map.get("transitive").asInstanceOf[Option[Boolean]].orElse(Option(true))

  val excludes: Option[List[Dependency]] = map.get("excludes") match {
    case Some(s) =>
      val buffer = new ListBuffer[Dependency]()
      s.asInstanceOf[List[Map[_,_]]].foreach(exclude =>{
         buffer += new Dependency(exclude.asInstanceOf[Map[String, String]])
      })
      Option(buffer.toList)
    case _ => None
  }


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

/**
 * Orders the dependences by conf, org, name.
 */
class LooseDependencyOrdering extends Ordering[Dependency] {
  def compare(x: Dependency, y: Dependency) = new CompareToBuilder()
        .append(x.conf.getOrElse(null), y.conf.getOrElse(null))
        .append(x.org.getOrElse(null), y.org.getOrElse(null))
        .append(x.name.getOrElse(null), y.name.getOrElse(null))
        .toComparison
}