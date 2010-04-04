package org.brzy.plugin

import org.brzy.application.{WebApp => BrzyApp}
import org.brzy.config.Dependency

/**
 * <ul>
 * <li>Libraries included with other declared libraries</li>
 * <li> Configuration defaults which can be overriden by the configuration file for
 *    each environment.</li>
 * <li>services made available to controllers and other services.</li>
 * <li>resources added to the webapp directory.</li>
 * <li>web.xml file parsing</li>
 * <li>scripts made available to the brzy command</li>
 * </ul>
 * @author Michael Fortin
 * @version $Id: $
 */

trait Plugin {

  // name and version are supplied by the configuration file bundled with the jar
//  val name = _
//
//  val version = _
//
  // called at application startup after service and controller wiring
  def bootstrap(app:BrzyApp):Unit = {}

  // additional library dependencies
  def dependencies:Array[Dependency] = Array()

  // additional configuration properties
  def configuration:Map[String,String] = Map()

  // services to install
  def services:List[_] = List()

  // list all deployment resources
  // available in the configuartion file
//  def resources:List[String] = List()

  // tokenizer to parse resource, returns whole file if there is none to parse
  def webxml(tokenizer:String):Unit = {}

  // scripts to install in the application
  // available in the configuation file
//  def scripts:List[String] = List()

}