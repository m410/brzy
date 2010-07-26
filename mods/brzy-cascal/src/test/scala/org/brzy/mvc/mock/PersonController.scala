package org.brzy.mock

import org.brzy.controller.{Path, Controller}
import org.brzy.action.args.Parameters


@Controller("persons")
class PersonController {

  @Path("") def list =  {}
  @Path("{id}") def show(p:Parameters) = "person"->Person.get(p("id")(0))

}