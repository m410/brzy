package org.brzy.spring

import org.brzy.config.mod.Mod

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
class SpringModConfig(map: Map[String, AnyRef]) extends Mod(map) {
  override val configurationName = "Email Configuration"
  val applicationContext: Option[String] = map.get("application_context").asInstanceOf[Option[String]].orElse(None)

  override def <<(that: Mod): Mod = {
    if (that == null) {
      this
    }
    else if (that.isInstanceOf[SpringModConfig]) {
      val it = that.asInstanceOf[SpringModConfig]
      new SpringModConfig(Map[String, AnyRef](
        "application_context" -> it.applicationContext.getOrElse(this.applicationContext.getOrElse(null)))
              ++ super.<<(that).asMap)
    }
    else {
      new SpringModConfig(Map[String, AnyRef](
        "application_context" -> that.map.get("application_context").getOrElse(this.applicationContext.getOrElse(null)))
              ++ super.<<(that).asMap)
    }
  }

  override def asMap = map
}