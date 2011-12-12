package org.brzy.webapp.action.args

import javax.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.brzy.webapp.action.Action
import org.brzy.application.WebApp
import org.brzy.webapp.controller.Permission

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
object ArgsBuilder {
  val log = LoggerFactory.getLogger(getClass)
  private val ParametersClass = classOf[Parameters]
  private val CookiesClass = classOf[Cookies]
  private val PostBodyClass = classOf[MultipartForm]
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
        new MultipartRequest(req)
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