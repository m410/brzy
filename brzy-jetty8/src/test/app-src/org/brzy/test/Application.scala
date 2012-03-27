package org.brzy.test

import org.brzy.application.WebAppConfiguration
import org.brzy.application.WebApp

class Application (config:WebAppConfiguration) extends WebApp(config) {
  def makeServices = List.empty[AnyRef]
  def makeControllers = List(proxyInstance[HomeController]()
  )
}
