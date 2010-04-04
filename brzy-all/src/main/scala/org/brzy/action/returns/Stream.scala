package org.brzy.action.returns

import xml.Elem

/**
 * @author Michael Fortin
 * @version $Id: $
 */
abstract class Stream 

  case class Xml(elem:Elem) extends Stream

  case class Text(text:String) extends Stream

  case class Bytes(bytes:Array[Byte]) extends Stream

  case class Json(content:String) extends Stream
