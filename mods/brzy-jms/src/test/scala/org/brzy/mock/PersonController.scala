package org.brzy.mock

import org.brzy.controller.{Path, Controller}

/**
 * @author Michael Fortin
 * @version $Id: $
 */
@Controller("persons/")
class PersonController {

  @Path("")
  def list =  {}

}