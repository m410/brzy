package org.brzy.mvc.action.returns

/**
 * @author Michael Fortin
 * @version $Id: $
 */
abstract class Data

  case class Model(attrs:Tuple2[String,AnyRef]*) extends Data

  case class CookieAdd(attrs:Tuple2[String,AnyRef]) extends Data

  case class SessionAdd(attrs:Tuple2[String,AnyRef]*) extends Data

  case class SessionRemove(attr:String) extends Data

  case class Flash(code:String,default:String) extends Data
