package org.brzy.mvc.action.args

import collection.immutable._

/**
 * @author Michael Fortin
 */
class Parameters(map:collection.Map[String,Array[String]])
    extends Map[String,String] with MapLike[String, String, Parameters] {

  override def empty:Parameters = new Parameters(Map[String,Array[String]]())

  override def get(key: String): Option[String] =
    if(map.contains(key))
      Option(map(key)(0))
    else
      None

  def array(key:String):Option[Array[String]] = map.get(key)
  
  override def iterator: Iterator[(String, String)] = Iterator(map.map(f=> f._1 -> f._2(0)).toArray:_*)

  // does nothing
  override def + [B1 >: String](kv: (String, B1)): Parameters = this

  // does nothing
  override def -(key: String): Parameters =  this
}

