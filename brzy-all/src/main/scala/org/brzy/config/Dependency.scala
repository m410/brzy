package org.brzy.config

import reflect.BeanProperty

/**
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
class Dependency {
  @BeanProperty var provided:String =_
  @BeanProperty var compile:String =_
  @BeanProperty var test:String =_

  private var internal:String = _
  private var confIn:String = _
  private var orgIn:String = _
  private var nameIn:String = _
  private var revIn:String = _

  private def setup():Unit = {
    if(internal == null) {
      if(provided != null) {
        internal = provided
        confIn = "provided"
      }
      else if(compile != null) {
        internal = compile
        confIn = "compile"
      }
      else if(test != null) {
        internal = test
        confIn = "test"
      }
      val split = internal.split(":")
      orgIn = split(0)
      nameIn = split(1)
      revIn = split(2)      
    }
  }

  def org():String = {setup();orgIn}
  def name():String = {setup();nameIn}
  def rev():String = {setup();revIn}
  def conf():String = {setup();confIn}
}