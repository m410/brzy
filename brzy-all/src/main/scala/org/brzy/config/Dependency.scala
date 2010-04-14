package org.brzy.config

import reflect.BeanProperty

/**
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
class Dependency {
  @BeanProperty var lib:String =_

  private var confIn:String = _
  private var orgIn:String = _
  private var nameIn:String = _
  private var revIn:String = _

  private def setup():Unit = {
    if(confIn == null) {
      val split = lib.split(":")
      confIn = split(0)
      orgIn = split(1)
      nameIn = split(2)
      revIn = split(3)
    }
  }

  def org():String = {setup();orgIn}
  def name():String = {setup();nameIn}
  def rev():String = {setup();revIn}
  def conf():String = {setup();confIn}


  override def toString = lib
}