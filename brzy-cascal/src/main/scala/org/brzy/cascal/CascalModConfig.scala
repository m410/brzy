package org.brzy.cascal

import org.brzy.config.mod.Mod

/**
 *
 * @author Michael Fortin
 */
class CascalModConfig(map: Map[String, AnyRef]) extends Mod(map) {
  override val configurationName = "Cascal Configuration"
  val userName: Option[String] = map.get("user_name").asInstanceOf[Option[String]].orElse(None)
  val password: Option[String] = map.get("password").asInstanceOf[Option[String]].orElse(None)
  val keySpace: Option[String] = map.get("key_space").asInstanceOf[Option[String]].orElse(None)
  val keyFamily: Option[String] = map.get("key_family").asInstanceOf[Option[String]].orElse(None)
  
  override def <<(that: Mod):Mod = {
    if (that == null) {
      this
    }
    else if(that.isInstanceOf[CascalModConfig]) {
      val it = that.asInstanceOf[CascalModConfig]
      new CascalModConfig(Map[String, AnyRef](
        "user_name" -> it.userName.getOrElse(this.userName.getOrElse(null)),
        "password" -> it.password.getOrElse(this.password.getOrElse(null)),
        "key_space" -> it.keySpace.getOrElse(this.keySpace.getOrElse(null)),
        "key_family" -> it.keyFamily.getOrElse(this.keyFamily.getOrElse(null)))
        ++ super.<<(that).asMap)
    }
    else {
      new CascalModConfig(Map[String, AnyRef](
        "user_name" -> that.map.get("user_name").getOrElse(this.userName.getOrElse(null)),
        "password" -> that.map.get("password").getOrElse(this.password.getOrElse(null)),
        "key_space" -> that.map.get("key_space").getOrElse(this.keySpace.getOrElse(null)),
        "key_family" -> that.map.get("key_family").getOrElse(this.keyFamily.getOrElse(null)))
        ++ super.<<(that).asMap)
    }
  }

  override def asMap = map

}