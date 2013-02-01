package org.brzy.exp

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
sealed trait FilterDuty

case class ActOn(a:Action) extends FilterDuty

case class SecureRedirect(p:String) extends FilterDuty

case class AuthenticateRedirect(p:String) extends FilterDuty

case class DispatchTo(p:String) extends FilterDuty

case object NotAnAction extends FilterDuty
