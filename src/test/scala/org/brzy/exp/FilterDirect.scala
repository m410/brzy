package org.brzy.exp

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
sealed trait FilterDirect

case class ActOn(a:Action)                  extends FilterDirect

case class ActOnAsync(a:Action)             extends FilterDirect

case class RedirectToSecure(p:String)       extends FilterDirect

case class RedirectToAuthenticate(p:String) extends FilterDirect

case class DispatchTo(p:String)             extends FilterDirect

case object NotAnAction                     extends FilterDirect
