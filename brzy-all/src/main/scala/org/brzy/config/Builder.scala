package org.brzy.config

import org.ho.yaml.Yaml
import java.net.URL
import java.lang.String
import java.io._

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class Builder(appFile:File, environment:String) {

  def this(conf:String, environment:String) = this(new File(conf),environment)

  def this(conf:URL, environment:String) =  this(new File(conf.getFile),environment)

  private val dev = "developement"
  private val prod = "production"
  private val test = "test"

  assert(appFile != null,"configuration file is null")
  assert(appFile.exists,"configuration file doesn not exist: " + appFile.getAbsolutePath)

  private val defaultConfig = Yaml.loadType(
    getClass().getClassLoader().getResourceAsStream("brzy-app.default.b.yml"),
    classOf[Config])

  private val appConfig = defaultConfig.merge(Yaml.loadType(appFile, classOf[Config]))

  val config = environment match {
    case "development" =>
      appConfig.merge(appConfig.environment_overrides.find(_.environment == dev).get)
    case "test" =>
      appConfig.merge(appConfig.environment_overrides.find(_.environment == test).get)
    case "production" =>
      appConfig.merge(appConfig.environment_overrides.find(_.environment == prod).get)
    case _ =>
      error("Unknown Environment: " + environment)
  }

}
