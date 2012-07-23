package org.brzy.mod.migrate

/**
 * A set of alteration factories that get run on a specific version of your application.
 *
 * @author Michael Fortin
 */
trait Alterations {
  def alterationFactories:List[AlterationFactory]
}

trait AlterationFactory

trait Alteration {
  def up()

  def down()
}
