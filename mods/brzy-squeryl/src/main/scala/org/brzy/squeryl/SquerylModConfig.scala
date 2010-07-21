package org.brzy.squeryl

import org.brzy.config.mod.Mod

/**
 * @author Michael Fortin
 */
class SquerylModConfig(map: Map[String, AnyRef]) extends Mod(map) {
  override val configurationName = "Squeryl"

  val driver: Option[String] = map.get("driver").asInstanceOf[Option[String]].orElse(None)
  val url: Option[String] = map.get("url").asInstanceOf[Option[String]].orElse(None)
  val userName: Option[String] = map.get("user_name").asInstanceOf[Option[String]].orElse(None)
  val password: Option[String] = map.get("password").asInstanceOf[Option[String]].orElse(None)
  val adaptorName: Option[String] = map.get("adaptor_name").asInstanceOf[Option[String]].orElse(None)


  override def <<(that: Mod):Mod  = {
    if (that == null) {
      this
    }
    else if(that.isInstanceOf[SquerylModConfig]) {
      val it = that.asInstanceOf[SquerylModConfig]
      new SquerylModConfig(Map[String, AnyRef](
        "driver" -> it.driver.getOrElse(this.driver.getOrElse(null)),
        "url" -> it.url.getOrElse(this.url.getOrElse(null)),
        "user_name" -> it.userName.getOrElse(this.userName.getOrElse(null)),
        "password" -> it.password.getOrElse(this.password.getOrElse(null)),
        "adaptor_name" -> it.adaptorName.getOrElse(this.adaptorName.getOrElse(null)))
        ++ super.<<(that).asMap)
    }
    else {
      new SquerylModConfig(Map[String, AnyRef](
        "driver" -> that.map.get("driver").getOrElse(this.driver.getOrElse(null)),
        "url" -> that.map.get("url").getOrElse(this.url.getOrElse(null)),
        "user_name" -> that.map.get("user_name").getOrElse(this.userName.getOrElse(null)),
        "password" -> that.map.get("password").getOrElse(this.password.getOrElse(null)),
        "adaptor_name" -> that.map.get("adapter_name").getOrElse(this.adaptorName.getOrElse(null)))
        ++ super.<<(that).asMap)
    }
  }

  override def asMap:Map[String,AnyRef] = map
}