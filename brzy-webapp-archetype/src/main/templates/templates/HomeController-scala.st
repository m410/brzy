package $package$

import org.brzy.webapp.controller.{Action,Controller}
import org.brzy.webapp.action.returns.View

/**
 */
@Controller("")
class HomeController {
  @Action("") def index = View("/index")
}