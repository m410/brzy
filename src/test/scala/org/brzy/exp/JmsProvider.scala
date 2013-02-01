package org.brzy.exp

import org.brzy.fab.mod.ModProvider

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
trait JmsProvider extends Application with ModProvider {
  def name = "JmsProvider"
  abstract override def moduleProviders = super.moduleProviders ++ Seq(this)

  abstract override def startup() {
    super.startup()
  }

  abstract override def shutdown() {
    super.shutdown()
  }
}
