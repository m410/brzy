package org.brzy.ejb

import org.brzy.config.mod.Mod
import collection.mutable.ListBuffer
import java.lang.String
import collection.immutable.Map

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
class EjbModConfig(map: Map[String, AnyRef]) extends Mod(map) {
  override val configurationName = "Ejb Configuration"
  val jndi: Option[String] = map.get("jndi").asInstanceOf[Option[String]].orElse(None)

  val beans: List[EjbBean] = {
    if (map.contains("beans")) {
      val bs = map("beans")
      println("bs: " + bs)
      val buf = ListBuffer[EjbBean]()
      bs.asInstanceOf[List[Map[String, String]]].foreach((b: Map[String, String]) => {
        buf += EjbBean(b("service_name"), b("remote_interface"), b("jndi_name"))
      })
      buf.toList
    }
    else {
      Nil
    }
  }

  /*
 beans:
   - service_name: myService
     remote_interface: com.somecompany.service.MyServiceRemote
     jndi_name: java:comp/env/myservice
  */
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