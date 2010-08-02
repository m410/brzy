package org.brzy.jms

import org.brzy.config.mod.Mod

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
class JmsModConfig(map: Map[String, AnyRef]) extends Mod(map) {
  override val configurationName = "JMS Configuration"
  val connectionFactoryClass:Option[String] = map.get("connection_factory_class").asInstanceOf[Option[String]].orElse(None)
  val connectionUrl:Option[String] = map.get("connection_url").asInstanceOf[Option[String]].orElse(None)
  
  override def <<(that: Mod): Mod = {
    if (that == null) {
      this
    }
    else if (that.isInstanceOf[JmsModConfig]) {
      val it = that.asInstanceOf[JmsModConfig]
      new JmsModConfig(Map[String, AnyRef](
        "connection_factory_class" -> it.connectionFactoryClass.getOrElse(this.connectionFactoryClass.getOrElse(null)),
        "connection_url" -> it.connectionUrl.getOrElse(this.connectionUrl.getOrElse(null)))
              ++ super.<<(that).asMap)
    }
    else {
      new JmsModConfig(Map[String, AnyRef](
        "connection_factory_class" -> that.map.get("connection_factory_class").getOrElse(this.connectionFactoryClass.getOrElse(null)),
        "connection_url" -> that.map.get("connection_url").getOrElse(this.connectionUrl.getOrElse(null)))
              ++ super.<<(that).asMap)
    }
  }

  override def asMap = map
}