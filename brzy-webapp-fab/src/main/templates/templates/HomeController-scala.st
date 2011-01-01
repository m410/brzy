package $package$

import org.brzy.webapp.controller.Controller
import org.brzy.webapp.action.Action
import org.brzy.webapp.action.returns._
import org.brzy.webapp.action.args._

/**
 *
 */
class HomeController extends Controller(""){
  val actions = List(Action("","index", index _))
  def index = "name" -> "value"
}