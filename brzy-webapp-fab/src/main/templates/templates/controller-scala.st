package $package$

import org.brzy.webapp.controller.Controller
import org.brzy.webapp.action.Action
import org.brzy.webapp.action.returns._
import org.brzy.webapp.action.args._

/**
 *
 */
class $className$ extends Controller("$className$s"){
  val actions = List(Action("","index", index _))
  def index = "name" -> "value"
}