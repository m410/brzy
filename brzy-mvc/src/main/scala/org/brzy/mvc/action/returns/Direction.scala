package org.brzy.action.returns

//import com.twitter.json.{Json=>tJson}

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

  case class Xml(t:AnyRef) extends Direction with Parser {
    def parse = {<class>{t.getClass.getSimpleName}</class>}.toString
    val contentType = "text/xml"
  }

  case class Text(ref:AnyRef) extends Direction with Parser{
    def parse = ref.toString
    val contentType = "text"
  }

  case class Binary(bytes:Array[Byte], contentType:String) extends Direction

  case class Json(t:AnyRef) extends Direction with Parser {
    def parse = "{}"//tJson.build(Map("class"->t.getClass.getSimpleName))
    val contentType = "text/json"
  }

  case class Error(code:Int, msg:String) extends Direction