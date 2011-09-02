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
package org.brzy.webapp.controller

import collection.mutable.Buffer
import java.util.regex.Pattern
import org.slf4j.LoggerFactory

/**
 * A representation of a RESTful like path.  This is used to compare paths of actions to see
 * if they're eligible for execution on that action.
 *
 * @author Michael Fortin
 */
case class Path(base: String, sub: String) extends Ordered[Path] {

  protected[controller] val log = LoggerFactory.getLogger(classOf[Path])

  protected[controller] val path = {
    val combined =
      if(sub.equals(""))
        base
      else if (!sub.startsWith("/"))
        base + "/" + sub
      else
        base + sub

    val preSlash =
      if (!combined.startsWith("/"))
        "/" + combined
      else
        combined

    preSlash.replaceAll("//", "/")
  }

  protected[controller] val strPattern = {
    // TODO strip out curlies, replace with embedded patter if it has a colon, otherwise use below
    "^" + path.replaceAll("""\{.*?\}""", """(.*?)""") + "$"
  }

  protected[controller] val pattern = strPattern.r

  def isMatch(contextPath: String) = {
    val actionTokens: Array[String] = path.split("/")
    val urlTokens: Array[String] = contextPath.replaceAll("//", "/").split("/")

    if (actionTokens.size == urlTokens.size) {
      var map = Map[String, String]()

      for (x <- 0 to urlTokens.size - 1)
        map += urlTokens(x) -> actionTokens(x)

      map.forall((nvp) => nvp._1 == nvp._2 || nvp._2.startsWith("{"))
    }
    else
      false
  }

  def extractParameterValues(contextPath: String) = {
    val buffer = Buffer[String]()
    val matcher = Pattern.compile(strPattern).matcher(contextPath)

    if (matcher.find)
      for (i <- 1 to matcher.groupCount)
        buffer += matcher.group(i)

    buffer.toArray
  }

  val parameterNames = {
    val buffer = Buffer[String]()
    val matcher = Pattern.compile("""\{(.*?)\}""").matcher(path)

    // todo Pull out named parameters, up to an optional colon
    while (matcher.find)
      buffer += matcher.group(1)

    buffer.toArray
  }

  def compare(that: Path) = this.path.compareTo(that.path)
}