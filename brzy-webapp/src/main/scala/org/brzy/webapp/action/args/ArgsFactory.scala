package org.brzy.webapp.action.args

import javax.servlet.http.HttpServletRequest
import org.brzy.webapp.action.Action
import org.slf4j.LoggerFactory

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
object ArgsFactory {
  val log = LoggerFactory.getLogger(getClass)

  def apply(req:HttpServletRequest,action:Action) = {
    val path = parseActionPath(req.getRequestURI, req.getContextPath)
    val args = action.argTypes
    log.trace("action:", args)
    log.trace("arg types: {}, path: {}", args, path)

    args.map(arg => arg match {
      case a:Attributes =>
        val extractParameterValues = action.path.extractParameterValues(path)
        val map = extractParameterValues.zipWithIndex.map({case (v,idx) => {
          action.path.parameterNames(idx) -> v
        }}).toMap
        new AttributeRequest(req, map)
      case a:Cookies =>
        new CookiesRequest(req)
      case a:PostBody =>
        new PostBodyRequest(req)
      case a:Principal =>
        new PrincipalRequest(req)
      case a:Properties =>
        new PropertiesRequest(req)
      case _ =>
        error("unknown action argument type: " + arg)
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