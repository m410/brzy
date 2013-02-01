package org.brzy.exp

import org.brzy.webapp.action.response.{NoView, View, Response, Direction}
import org.brzy.webapp.action.Constraint
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
trait Action extends Ordered[Action] {
  def pathExpr:String
  def transaction:Transaction
  def constraints:Seq[Constraint]
  def defaultView:Direction
  def controller:Controller
  def execute:()=>Array[Response]

  def compare(that: Action) = pathExpr.compareTo(that.pathExpr)

  def isMatch(method:String, contentType:String, path:String) = false

  def doService(request:HttpServletRequest, response:HttpServletResponse) {

  }
}

object Action {
  def apply(
          s:String,
          a:()=>Array[Response],
          view:Direction = NoView,
          t:Transaction = new Transaction,
          c:Seq[Constraint] = Seq.empty[Constraint])
          (implicit ctl:Controller) = {
    new Action {
      val pathExpr = s
      val transaction = t
      val constraints = c
      val defaultView = view
      val controller = ctl
      val execute = a
    }
  }
}