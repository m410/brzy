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
package org.brzy.mod.ejb

import collection.mutable.ListBuffer
import java.lang.String
import collection.immutable.Map
import org.brzy.fab.conf.BaseConf
import org.brzy.fab.mod.RuntimeMod

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
class EjbModConfig(override val map: Map[String, AnyRef]) extends RuntimeMod(map) {
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
  override def <<(that: BaseConf)= {
    if (that == null) {
      this
    }
    else {
      new EjbModConfig(Map[String, AnyRef](
        "jndi" -> that.map.getOrElse("jndi",this.jndi.orNull)
        ) ++ super.<<(that).map)
    }
  }
}