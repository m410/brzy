package org.brzy.exp

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
trait JmsProvider extends Application {
  abstract override val moduleProviders = super.moduleProviders ++ Seq(this)
}
