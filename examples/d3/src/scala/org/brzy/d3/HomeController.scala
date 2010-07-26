package org.brzy.d3

import org.brzy.controller.{Path,Controller}
import org.brzy.mvc.action.returns.View

/**
 */
@Controller("")
class HomeController {

	@Path("")
	def index = View("/index")
}