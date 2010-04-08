package org.brzy.action.returns

import xml.Elem

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

  case class Xml(elem:Elem) extends Direction

  case class Text(text:String) extends Direction

  case class Bytes(bytes:Array[Byte]) extends Direction

  case class Json(content:String) extends Direction