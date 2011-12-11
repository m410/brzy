package org.brzy.webapp.action.args

import javax.servlet.http.HttpServletRequest

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
trait Cookies  extends Arg {
  def list:List[Cookie]
}

case class Cookie(comment: String, domain: String, maxAge: Int, name: String,
        path: String, secure: Boolean, value: String, version: Int)


class CookiesRequest protected (request:HttpServletRequest) extends Cookies {
  def list = {
      if (request.getCookies == null || request.getCookies.length == 0)
        List.empty[Cookie]
      else
        request.getCookies.map(c => {
          Cookie(c.getComment, c.getDomain, c.getMaxAge, c.getName, c.getPath, c.getSecure, c.getValue, c.getVersion)
        }).toList
  }
}
