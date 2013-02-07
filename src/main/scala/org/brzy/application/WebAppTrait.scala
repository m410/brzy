package org.brzy.application

import org.brzy.controller.Controller
import org.brzy.action.args.{PrincipalRequest, ArgsBuilder}
import org.brzy._
import action.Action

import fab.interceptor.ManagedThreadContext
import fab.mod.{ViewModProvider, ModProvider}

import javax.servlet.http.HttpServletRequest

import org.slf4j.LoggerFactory


/**
 * This is the top of the tree of the application configuration.  All other application
 * module providers inherit form this along with the WebApp class.  It's the foundation of
 * a Cake pattern like assembly of the app.
 * 
 * @author Michael Fortin
 */
trait WebAppTrait {

  private val log = LoggerFactory.getLogger(getClass)

  val conf: WebAppConfiguration

  /**
   * The application class, hold information like the application name and version.
   */
  val application: org.brzy.fab.conf.Application

  /**
   * A flag to tell if ssl is enabled.  It can be turned on and off for different environments.
   */
  val useSsl:Boolean

  /**
   * The services for the application.  It's for things like jms listeners and cron schedulers.
   */
  def services:List[AnyRef]

  /**
   * The controllers for the application.
   * @see org.brzy.webapp.controller.controller
   * @return
   */
  def controllers:List[Controller]

  /**
   * @param request
   * @return
   */
  def doFilterAction(request:HttpServletRequest):FilterDirect

  /**
   *
   * @param request
   * @return
   */
  def serviceAction(request:HttpServletRequest):Option[Action]


  def moduleProviders:List[ModProvider]

  def persistenceProviders:List[ModProvider]

  def viewProvider:Option[ViewModProvider]

  def threadLocalSessions:List[ManagedThreadContext]

  /**
   * Actions are lazily assembled once the application is started. The actions are collected
   * from the available controllers.
   */
  val actions:List[Action]

  def startup()

  def shutdown()
}
