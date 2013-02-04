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
package org.brzy.action

import collection.mutable.Buffer
import java.util.regex.Pattern

/**
 *  * A representation of a RESTful like path.  This is used to compare paths of actions to see
 * if they're eligible for execution on the action.
 *
 * @author Michael Fortin
 * @param ctlrBase The path of the controller
 * @param actionBase The path of the action
 */
case class Path(ctlrBase: String, actionBase: String) extends Ordered[Path] {

  /**
   * The combined controller and action paths.
   */
  protected val path = {
    val combined =
      if(actionBase.equals(""))
        ctlrBase
      else if (!actionBase.startsWith("/"))
        ctlrBase + "/" + actionBase
      else
        ctlrBase + actionBase

    val preSlash =
      if (!combined.startsWith("/"))
        "/" + combined
      else
        combined

    preSlash.replaceAll("//", "/")
  }

  /**
   * Extracts the attribute patterns out of the url
   */
  protected val patternTokens = path.split("""\{|\}""").map(it=>{
      if(!it.contains("/") && it.contains(":"))
        "(" + it.split(":")(1) + ")"
      else if(!it.contains("/"))
        """(.*?)"""
      else
        it
    })

	/**
	 * Splits the url into parts for comparison with the request uri.
	 */
  protected val pathTokens = path.replaceAll("//", "/").split("/")

  /**
   * strip out curly braces, replace with embedded regex pattern if it has a colon, otherwise use
   * regex wildcard.
   */
  protected val pathPattern = "^" + patternTokens.foldLeft("")((a,b)=>a+b) + "$"

  /**
   * Create a regex expression out of the pattern path.
   */
  protected val pathMatcher = pathPattern.r

  /**
   * Check to see of the request uri matches the path expression of this action.
   *
   * @param contextPath The path to compare to this actions path.
   * @return true if it matches
   */
  def isMatch(contextPath: String) = {
    val urlTokens: Array[String] = contextPath.replaceAll("//", "/").split("/")
    if (pathTokens.size == urlTokens.size) {
      val tokens = pathTokens.zip(urlTokens)
      tokens.forall({case (a,b) =>
				if (isPattern(a))
          toPattern(a).findFirstIn(b).isDefined
        else 
					a == b
      })
    }
    else
      false
  }

  /**
   * check each path token to see if it's a url path variable.
   */
  private[this] def isPattern(a:String) = a.endsWith("}") && a.startsWith("{")

  protected def toPattern(a:String) = {
    val minusCurlies = a.substring(1,a.length() -1)

    if (minusCurlies.contains(":"))
      minusCurlies.substring(minusCurlies.indexOf(":")+1,minusCurlies.length()).r
    else
      "(.*?)".r
  }

  def extractParameterValues(contextPath: String) = {
    val buffer = Buffer[String]()
    pathMatcher.findFirstMatchIn(contextPath) match {
      case Some(matcher) =>
        for (i <- 1 to matcher.groupCount)
          buffer += matcher.group(i)        
      case _ =>
    }
    buffer.toArray
  }

  /**
   * Pull out named parameters, up to an optional colon
   */
  val parameterNames = {
    val buffer = Buffer[String]()
    val curlyMatcher = Pattern.compile("""\{(.*?)\}""").matcher(path)

    while (curlyMatcher.find) {
      val id = curlyMatcher.group(1)

      if (id.contains(":"))
        buffer += id.substring(0,id.indexOf(":"))
      else
        buffer += id
    }

    buffer.toArray
  }

  def compare(that: Path) = this.path.compareTo(that.path)
}