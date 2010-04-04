package org.brzy.action.returns

/**
 * Where you want to send the result of the action too.
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
abstract class Direction

  case class View(path:String) extends Direction

  case class Forward(path:String) extends Direction

  case class Redirect(path:String) extends Direction