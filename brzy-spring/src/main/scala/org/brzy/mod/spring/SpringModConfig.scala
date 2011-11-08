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
package org.brzy.mod.spring

import org.brzy.fab.mod.RuntimeMod
import org.brzy.fab.conf.BaseConf
import java.io.PrintWriter

/**
 * Spring module configuration.
 *
 * @author Michael Fortin
 */
class SpringModConfig(override val map: Map[String, AnyRef]) extends RuntimeMod(map) {
  val applicationContext: Option[String] = map.get("application_context").asInstanceOf[Option[String]].orElse(None)

  override def <<(that: BaseConf) =
    if (that == null)
      this
    else
      new SpringModConfig(Map[String, AnyRef](
        "application_context" -> that.map.getOrElse("application_context", this.applicationContext.orNull)
        ) ++ super.<<(that).map)

  override def prettyPrint(t: String, pw: PrintWriter) {
    val tab = t + "  "
    super.prettyPrint(tab,pw)
    pw.print("application_context: ")
    pw.println(applicationContext.getOrElse("<None>"))
  }
}