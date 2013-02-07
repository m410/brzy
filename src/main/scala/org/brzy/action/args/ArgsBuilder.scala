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
package org.brzy.action.args

import javax.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.brzy.action.Action

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

  def apply(req: HttpServletRequest, action: Action): Array[Arg] = {
    val actionPath = parseActionPath(req.getRequestURI, req.getContextPath)
    val args = action.argTypes
    log.trace("arg types: '{}', path: '{}'", Array(args, actionPath): _*)

    args.map(arg => arg match {
      case ParametersClass =>
        new ParametersRequest(req, action.paramsFor(actionPath.path))
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
    })
  }

  /**
   * TODO Copied form action, need to fix this.
   */
  def parseActionPath(uri: String, ctx: String):ActionPath = {

    val newuri = uri
            .replace(ctx,"")
            .replaceAll("^([\\w\\.])","/$1") // fix removal of first slash
            .replaceAll("/+","/") // replace all double slashes
            .replace(".brzy_async","") // remove extnsion
            .replace(".brzy","") // remove other extension
            .trim()

    if (uri.endsWith(".brzy"))
      ActionPath(newuri, true, false)
    else if(uri.endsWith(".brzy_async"))
      ActionPath(newuri, true, true)
    else
      ActionPath(newuri,  false, false)
  }
}