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

import org.springframework.context.support.ClassPathXmlApplicationContext
import collection.JavaConversions._
import collection.mutable.LinkedHashMap
import org.brzy.fab.mod.ModProvider

/**
 * Creates a spring application context and adds all the beans to the application.
 *
 * @see http ://static.springsource.org/spring/docs/3.0.3.RELEASE/spring-framework-reference/html/
 * @author Michael Fortin
 */
class SpringModProvider(c: SpringModConfig) extends ModProvider {
  val name = c.name.get
  val context = new ClassPathXmlApplicationContext(c.applicationContext.get)

  override val serviceMap = {
    val map = LinkedHashMap[String, AnyRef]()
    context.getBeanDefinitionNames.foreach(x => map += x -> context.getBean(x).asInstanceOf[AnyRef])
    map.toMap
  }
}