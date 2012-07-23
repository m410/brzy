package org.brzy.mod.migrate

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
class Version(val mjr:Int, val mnr:Int, val sub:Int, val sub2:Int) extends Ordered[Version] {
  def compare(that: Version) = {
    this.mjr - that.mjr
  }
}

