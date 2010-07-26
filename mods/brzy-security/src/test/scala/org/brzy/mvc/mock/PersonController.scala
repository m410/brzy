package org.brzy.mock

import org.brzy.controller.{Path, Controller}
import org.brzy.action.args.Parameters
import org.brzy.security.Secured

@Controller("persons")
@Secured(Array("ROLE_USER","ROLE_ADMIN"))
class PersonController {

  @Path("") def list =  {}
  @Path("{id}") def show(p:Parameters) = "person"->Person.get(p("id")(0))

}