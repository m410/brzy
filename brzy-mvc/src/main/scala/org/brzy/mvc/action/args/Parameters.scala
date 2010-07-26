package org.brzy.action.args

import collection.immutable._
import collection.generic._

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class Parameters(map:collection.Map[String,Array[String]])
    extends Map[String,Array[String]]
    with MapLike[String, Array[String], Parameters] {

  override def empty:Parameters = null

  override def get(key: String): Option[Array[String]] = map.get(key)

  def str(key:String):Option[String] =
    if(map.contains(key))
        Some(map.get(key).get(0))
      else
        None
  
  override def iterator: Iterator[(String, Array[String])] = map.iterator

  // does nothing
  override def + [B1 >: Array[String]](kv: (String, B1)): Parameters = this

  // does nothing
  override def -(key: String): Parameters =  this
}

