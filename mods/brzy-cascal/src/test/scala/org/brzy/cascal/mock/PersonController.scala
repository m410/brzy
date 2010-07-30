package org.brzy.cascal.mock

import org.brzy.mvc.controller.{Path, Controller}
import org.brzy.mvc.action.args.Parameters


@Controller("persons")
class PersonController {

  @Path("") def list =  {}
  @Path("{id}") def show(p:Parameters) = "person"->Person.get(p("id")(0))

}