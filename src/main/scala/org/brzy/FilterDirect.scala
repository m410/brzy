package org.brzy

import action.Action


/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
sealed trait FilterDirect

case class ActOn(action:Action) extends FilterDirect

case class ActOnAsync(action:Action) extends FilterDirect

case class RedirectToSecure(path:String) extends FilterDirect

case class RedirectToAuthenticate(path:String) extends FilterDirect

case class DispatchTo(path:String) extends FilterDirect

case object NotAnAction  extends FilterDirect
