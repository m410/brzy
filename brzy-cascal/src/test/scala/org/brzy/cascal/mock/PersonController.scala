package org.brzy.cascal.mock

import org.brzy.mvc.controller.{Action, Controller}
import org.brzy.mvc.action.args.Parameters


@Controller("persons")
class PersonController {

  @Action("") def list =  {}
  @Action("{id}") def show(p:Parameters) = "person"->Person.get(p("id"))

}