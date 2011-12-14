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
package org.brzy.webapp.action.args

import javax.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.brzy.webapp.action.Action

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
object ArgsBuilder {
  val log = LoggerFactory.getLogger(getClass)
  private val ParametersClass = classOf[Parameters]
  private val CookiesClass = classOf[Cookies]
  private val PostBodyClass = classOf[PostBody]
  private val PrincipalClass = classOf[Principal]
  private val PropertiesClass = classOf[Properties]

  def apply(req:HttpServletRequest,action:Action):Array[Arg] = {
    val path = parseActionPath(req.getRequestURI, req.getContextPath)
    val args = action.argTypes
    log.trace("arg types: '{}', path: '{}'", args, path)

    args.map(arg => arg match {
      case ParametersClass =>
        val extractParameterValues = action.path.extractParameterValues(path)
        val map = extractParameterValues.zipWithIndex.map({case (v,idx) => {
          action.path.parameterNames(idx) -> v
        }}).toMap
        new ParametersRequest(req, map)
      case CookiesClass =>
        new CookiesRequest(req)
      case PostBodyClass =>
        new PostBodyRequest(req)
      case PrincipalClass =>
        new PrincipalRequest(req)
      case PropertiesClass =>
        new PropertiesRequest(req)
      case _ =>
        throw new UnknownActionArgException("Unknown action argument type: " + arg)
    }).toArray
  }

  /**
   * TODO Copied form action, need to fix this.
   */
  def parseActionPath(uri: String, ctx: String) = {
    val newuri =
      if (uri.startsWith("//"))
        uri.substring(1, uri.length)
      else
        uri

    if (newuri.endsWith(".brzy") && (ctx == "" || ctx == "/"))
      newuri.substring(0, newuri.length - 5)
    else if (newuri.endsWith(".brzy") && (ctx != "" || ctx != "/"))
      newuri.substring(ctx.length, newuri.length - 5)
    else if (!newuri.endsWith(".brzy") && (ctx != "" || ctx != "/"))
      newuri.substring(ctx.length, newuri.length)
    else
      newuri
  }
}