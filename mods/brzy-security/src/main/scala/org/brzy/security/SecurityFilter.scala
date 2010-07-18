package org.brzy.security

import javax.servlet._

/**
 *
 * 
 * @author Michael Fortin
 */
class SecurityFilter extends Filter{

  def init(filterConfig: FilterConfig) = {

  }

  def doFilter(request: ServletRequest, response: ServletResponse, filterChain: FilterChain) = {
    filterChain.doFilter(request,response)    
  }

  def destroy = {

  }

}