package org.brzy.jpa

import org.brzy.config.mod.Mod

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
class JpaModConfig(map: Map[String, AnyRef]) extends Mod(map) {
  override val configurationName = "JPA Configuration"
  val persistenceUnit: Option[String] = map.get("persistence_unit").asInstanceOf[Option[String]].orElse(None)


  override def <<(that: Mod): Mod = {
    if (that == null) {
      this
    }
    else if (that.isInstanceOf[JpaModConfig]) {
      val it = that.asInstanceOf[JpaModConfig]
      new JpaModConfig(Map[String, AnyRef](
        "persistence_unit" -> it.persistenceUnit.getOrElse(this.persistenceUnit.getOrElse(null)))
              ++ super.<<(that).asMap)
    }
    else {
      new JpaModConfig(Map[String, AnyRef](
        "persistence_unit" -> that.map.get("persistence_unit").getOrElse(this.persistenceUnit.getOrElse(null)))
              ++ super.<<(that).asMap)
    }
  }

  override def asMap = map
}