package org.brzy.mock

import org.brzy.application.WebApp
import org.brzy.config.webapp.WebAppConfig
import collection.immutable.SortedSet
import org.brzy.mvc.action.Action


class SecurityMockWebApp(config:WebAppConfig) extends WebApp(config) {
  override def makeServices = List()
  override def makeControllers = List(new PersonController)
  override lazy val actions = SortedSet(
    new Action("/persons/{id}", classOf[PersonController].getMethods.find(_.getName == "show").get, new PersonController, ".ssp")
    )
}