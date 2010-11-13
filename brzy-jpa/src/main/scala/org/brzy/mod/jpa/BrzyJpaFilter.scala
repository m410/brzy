package org.brzy.mod.jpa

import javax.servlet._
import org.brzy.application.WebApp

/**
 * Implements the Open session in view design pattern for jpa based entities.
 * 
 * @author Michael Fortin
 */
class BrzyJpaFilter extends Filter {

  var manager:JpaContextManager = _

  def init(config: FilterConfig) = {
    val webapp = config.getServletContext.getAttribute("application").asInstanceOf[WebApp]
    manager = webapp.persistenceProviders.find(_.name == "brzy-jpa") match {
      case Some(provider) => provider.asInstanceOf[JpaModProvider].interceptor
      case _ => error("cound not find the brzy-jpa provider")
    }
  }

  def doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) = {
    val session = manager.createSession

    try {
      chain.doFilter(req, res)
    }
    finally {
      manager.destroySession(session)
    }
  }

  def destroy = {}
}