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

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
trait Cookies  extends Arg {
  def list:List[CookieRequest]
}

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
case class CookieRequest(comment: String, domain: String, maxAge: Int, name: String,
        path: String, secure: Boolean, value: String, version: Int)

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
class CookiesRequest protected (request:HttpServletRequest) extends Cookies {
  def list = {
      if (request.getCookies == null || request.getCookies.length == 0)
        List.empty[CookieRequest]
      else
        request.getCookies.map(c => {
          CookieRequest(c.getComment, c.getDomain, c.getMaxAge, c.getName, c.getPath, c.getSecure, c.getValue, c.getVersion)
        }).toList
  }
}
