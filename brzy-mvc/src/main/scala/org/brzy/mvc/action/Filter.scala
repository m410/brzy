package org.brzy.mvc.action

import javax.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import javax.servlet.{FilterChain, FilterConfig, ServletResponse, ServletRequest, Filter => SFilter}

/**
 * Forwards only requests to brzy actions, lets all other pass through.
 *
 * @author Michael Fortin
 */
class Filter extends SFilter {
  private val log = LoggerFactory.getLogger(classOf[Filter])
  val pattern = """\.([\w\d]{1,4})$""".r

  def init(config: FilterConfig) = {
    log.info("Init Filter: {}", config)
  }

  def doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) = {
    val uri = req.asInstanceOf[HttpServletRequest].getRequestURI

    if(!(pattern findFirstMatchIn uri).isEmpty) { // pass it on if the url ends with any extension
      chain.doFilter(req,res)
    }
    else { // assume it's an action and append .brzy
      val ctx = req.asInstanceOf[HttpServletRequest].getContextPath
      val forward =
        if(ctx == "")
          uri.substring(0,uri.length)
        else
          uri.substring(ctx.length,uri.length)
      
      log.trace("forward: {}",forward)
      req.getRequestDispatcher(forward + ".brzy").forward(req,res)
    }
  }

  def destroy = {
    log.debug("Destroy")
  }
}