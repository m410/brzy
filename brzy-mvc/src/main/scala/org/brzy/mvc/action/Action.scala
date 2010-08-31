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
package org.brzy.mvc.action

import java.lang.reflect.Method
import collection.mutable.Buffer
import java.util.regex.Pattern
import org.slf4j.LoggerFactory


/**
 * @author Michael Fortin
 */
class Action(val path:String, val actionMethod:Method, val inst:AnyRef, val viewType:String )
        extends Ordered[Action] {
  private val log = LoggerFactory.getLogger(classOf[Action])
  val returnTypes =  actionMethod.getReturnType

  val parameterTypes = actionMethod.getParameterTypes

  val defaultView = {
    val clazz = inst.getClass
    val folder:String =
      if(clazz.getSimpleName.indexOf("Controller") == -1)
        clazz.getSimpleName
      else
        clazz.getSimpleName.substring(0,clazz.getSimpleName.indexOf("Controller"))
    "/" + folder.substring(0,1).toLowerCase + folder.substring(1) + "/"+ actionMethod.getName
  }

  private val strPattern = "^/*" + path.replaceAll("""\{.*?\}""","""(.*?)""") + "$"
  val pattern = strPattern.r

  def matchParameters(url:String) = {
    log.debug("match parameters: {}, {}",strPattern,url)
    val buffer = Buffer[String]()
    val matcher = Pattern.compile(strPattern).matcher(url)
    if(matcher.find)
      for(i <- 1 to matcher.groupCount)
        buffer += matcher.group(i)
    buffer.toArray
  }

  val matchParameterIds = {
    val buffer = Buffer[String]()
    val matcher = Pattern.compile("""\{(.*?)\}""").matcher(path)
    while(matcher.find)
        buffer += matcher.group(1)
    buffer.toArray
  }

  override def compare(that : Action) : Int =  path.compareToIgnoreCase(that.path)

  override def toString = "Action('" + path + "', " + actionMethod + ")"
}
