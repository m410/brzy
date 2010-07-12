package org.brzy.ejb

import org.brzy.config.mod.Mod

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
class EjbModConfig(map: Map[String, AnyRef]) extends Mod(map) {
  override val configurationName = "Ejb Configuration"
  val jndi: Option[String] = map.get("jndi").asInstanceOf[Option[String]].orElse(None)

  override def <<(that: Mod): Mod = {
    if (that == null) {
      this
    }
    else if (that.isInstanceOf[EjbModConfig]) {
      val it = that.asInstanceOf[EjbModConfig]
      new EjbModConfig(Map[String, AnyRef](
        "jndi" -> it.jndi.getOrElse(this.jndi.getOrElse(null)))
              ++ super.<<(that).asMap)
    }
    else {
      new EjbModConfig(Map[String, AnyRef](
        "jndi" -> that.map.get("jndi").getOrElse(this.jndi.getOrElse(null)))
              ++ super.<<(that).asMap)
    }
  }

  override def asMap = map
}