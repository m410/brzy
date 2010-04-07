package org.brzy.sample

import javax.ws.rs.Path
import org.brzy.action.returns.View

/**
 *
 */
@Path("/")
class HomeController {
	
	@Path("") 
	def index = View("/index.jsp")
}