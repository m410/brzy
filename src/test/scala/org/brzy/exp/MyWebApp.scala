package org.brzy.exp

import org.brzy.application.WebAppConfiguration

/**
 * perposed version of the application
 *
 */
class MyWebApp(conf: WebAppConfiguration) extends Application(conf) with EmailProvider with JmsProvider {

  override val services = super.services ++ List(
    new MyService with PersonStore
  )

  override val controllers = super.controllers ++ List(
    new MyController with PersonStore
  )
}