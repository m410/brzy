package org.brzy.security.mock

import org.brzy.mvc.controller.{Action, Controller}
import org.brzy.mvc.action.args.Parameters
import org.brzy.security.Secured

@Controller("persons")
@Secured(Array("ROLE_USER","ROLE_ADMIN"))
class PersonController {

  @Action("") def list =  {}
  @Action("{id}") def show(p:Parameters) = "person"->Person.get(p("id"))

}