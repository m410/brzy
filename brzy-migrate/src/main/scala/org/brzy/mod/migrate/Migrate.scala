package org.brzy.mod.migrate

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
trait Migrate {
  def version: String

  def up()

  def down()
}
