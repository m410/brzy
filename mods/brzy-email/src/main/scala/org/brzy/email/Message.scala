package org.brzy.email

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
abstract class Message
  case class SimpleMessage(to:Array[String],subject:String, body:String) extends Message