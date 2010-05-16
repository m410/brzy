package org.brzy.config

/**
 * @author Michael Fortin
 * @version $Id : $
 */
class Repository(m: Map[String, AnyRef]) extends Config(m) {

  val id: String = set[String](m.get("id"))
  val name: String = set[String](m.get("name"))
  val url: String = set[String](m.get("url"))
  val snapshots: Boolean = false
  val releases: Boolean = true


  val configurationName = "Repository"

  def asMap = {
    val map = Map[String, AnyRef]()
    // TODO add each property
    map
  }
}