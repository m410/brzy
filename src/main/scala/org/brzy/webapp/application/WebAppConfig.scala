package org.brzy.webapp.application

import java.io.{File, PrintWriter}
import org.slf4j.LoggerFactory

import org.brzy.fab.conf.{Yaml, Logging}
import org.brzy.fab.mod.{Mod, ProjectModuleConfiguration}
import java.util.MissingResourceException
import net.liftweb.json._
import scala.Some

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
class WebAppConfig(override val map: Map[String, AnyRef]) extends ProjectModuleConfiguration(map) {

  val useSsl: Boolean = map.get("use_ssl") match {
    case Some(e) => e.asInstanceOf[Boolean]
    case _ => false
  }

  val logging: Option[Logging] = map.get("logging") match {
    case Some(s) =>
      if (s != null)
        Option(new Logging(s.asInstanceOf[Map[String, AnyRef]]))
      else
        None
    case _ => None
  }

  val webXml: List[Map[String, AnyRef]] = map.get("web_xml") match {
    case Some(s) =>
      if (s != null && s.isInstanceOf[List[_]])
        s.asInstanceOf[List[Map[String, AnyRef]]]
      else
        List.empty[Map[String,AnyRef]]
    case _ => List.empty[Map[String,AnyRef]]
  }


  override def prettyPrint(t: String, pw: PrintWriter) {
    val tab = t + "  "
    super.prettyPrint(t,pw)
    pw.println("Use SSL: " + useSsl)
    pw.println("Logging")
    logging match {
      case Some(l) => l.prettyPrint(tab,pw)
      case _ => pw.println("<None>")
    }

    pw.println("web.xml")
    webXml.foreach(a=>pw.println(tab + a))
  }


  override def mergeConfiguration(t: ProjectModuleConfiguration) = {
    val that = t.asInstanceOf[WebAppConfig]
    val mergedWebXml = this.webXml ++ that.webXml
    val mergedUseSsl = if (this.useSsl) this.useSsl else that.useSsl

    val mergedLogging = {
      if (this.logging.isDefined) {
        this.logging.get << that.logging.orNull
      }.map
      else if (that.logging.isDefined)
        that.logging.get.map
      else
        null
    }

    val superMap = super.mergeConfiguration(that).map

    val newData = Map[String, AnyRef](
      "use_ssl" -> mergedUseSsl.asInstanceOf[AnyRef],
      "logging" -> mergedLogging,
      "web_xml" -> mergedWebXml
    ) ++ superMap

    instance(newData)
  }

  override def instance(m:Map[String,AnyRef]) = {
    new WebAppConfig(m)
  }
}

object WebAppConfig {

  private val log = LoggerFactory.getLogger(getClass)

  val defaultConfigFile = "/brzy-webapp.default.b.yml"

  val appConfigFile = "/brzy-webapp.b.yml"

  private def findEnv(a: AnyRef,env:String) = {
    a.asInstanceOf[Map[String, AnyRef]].find({
      case (k, v) => k == "environment" && v == env
    }).isDefined
  }

  def runtime(env: String, appConfig: String = appConfigFile, defaultConfig: String = defaultConfigFile) = {
    val asStream = getClass.getResourceAsStream(defaultConfig)
    val yaml = Yaml(asStream)
    val archetypeConfig = new WebAppConfig(yaml)

    // todo persistence modules will have lost some information, they're not the right instance
    val asStream1 = getClass.getResourceAsStream(appConfig)
    val yaml1 = Yaml(asStream1)
    val projectConfig = new WebAppConfig(yaml1)


    val envConfig = projectConfig.map.get("environment_overrides") match {
      case Some(ec) =>
        ec.asInstanceOf[List[Map[String,AnyRef]]].find(findEnv(_,env)) match {
          case Some(e) =>
            new WebAppConfig(e)
          case _ =>
            new WebAppConfig(Map.empty[String,AnyRef])
        }
      case _ =>
        new WebAppConfig(Map.empty[String,AnyRef])
    }
    val viewModule = makeRuntimeMod(projectConfig.views.getOrElse(archetypeConfig.views.orNull))
    val persistenceModules = projectConfig.persistence.map(makeRuntimeMod(_))
    val modules = projectConfig.modules.map(makeRuntimeMod(_))
    val m1a = archetypeConfig << projectConfig
    val m1 = m1a << viewModule
    val m2 = persistenceModules.foldLeft(m1)((r,c) => {
      r << c
    })
    val m3 = modules.foldLeft(m2)((r,c) => r << c)
    val m4 = m3 << envConfig
    m4.asInstanceOf[WebAppConfig]
  }

  def buildtime(modBaseDir: File, env: String, appConfigPath: String, defaultConfig: String = defaultConfigFile) = {
    val archetypeConfig = new WebAppConfig(Yaml(getClass.getResourceAsStream(defaultConfig)))
    val projectConfig = new WebAppConfig(Yaml(new File(appConfigPath)))
    val envConfig = projectConfig.map.get("environment_overrides") match {
      case Some(ec) =>
        ec.asInstanceOf[List[Map[String,AnyRef]]].find(findEnv(_,env)) match {
          case Some(e) =>
            new WebAppConfig(e.asInstanceOf[Map[String,AnyRef]])
          case _ =>
            new WebAppConfig(Map.empty[String,AnyRef])
        }
      case _ =>
        new WebAppConfig(Map.empty[String,AnyRef])
    }
    val viewModule = makeBuildTimeMod(projectConfig.views.getOrElse(archetypeConfig.views.orNull),modBaseDir)
    val persistenceModules = projectConfig.persistence.map(makeBuildTimeMod(_,modBaseDir))
    val modules = projectConfig.modules.map(makeBuildTimeMod(_,modBaseDir))
    val m1 = archetypeConfig << projectConfig << viewModule
    val m2 = persistenceModules.foldLeft(m1)((r,c) => r << c)
    val m3 = modules.foldLeft(m2)((r,c) => r << c)
    val m4 = m3 << envConfig
    m4.asInstanceOf[WebAppConfig]
  }

  protected def isRuntime(mod: Mod):Boolean = {
    val modResource: String = "modules/" + mod.name.get + "/brzy-module.b.yml"
    val cpUrl = getClass.getClassLoader.getResource(modResource)
    val yaml = Yaml(cpUrl.openStream)
    val result = yaml.get("mod_type") match {
      case Some(m) => m == "build"
      case _ => false
    }
    log.debug("{} is runtime: {}", mod.name, result)
    result
  }

  /**
   * Loads the application configuration from the classpath
   */
  protected def makeRuntimeMod(reference: Mod): Mod = {
    val modResource: String = "modules/" + reference.name.get + "/brzy-module.b.yml"
    log.debug("module conf: '{}'", modResource)

    val cpUrl = if (getClass.getClassLoader.getResource(modResource) != null)
        getClass.getClassLoader.getResource(modResource)
      else
        getClass.getClassLoader.getResource("/" + modResource)

    if (cpUrl == null) {
      val msg = "Cound not find the configuration for '" + modResource + "'" + " in classloader " + getClass.getClassLoader
      val cls = getClass.getName
      throw new MissingResourceException(msg, cls, modResource)
    }

    val yaml = Yaml(cpUrl.openStream)

    if (yaml.get("config_class").isDefined && yaml.get("config_class").get != null) {
      // this needs to be in this class, because of classloader scope issues.  Same
      // with the duplicate code below.  This should remove this later once the build runner
      // classloader is ironed out.
      val configClassName = yaml.get("config_class").get.asInstanceOf[String]
//      log.debug("config_class for mod: {}", configClassName)
      val c = Class.forName(configClassName)
      val constructor = c.getConstructor(Array(classOf[Map[_, _]]): _*)
      val modInst = constructor.newInstance(yaml).asInstanceOf[Mod]
      val mod = modInst << reference
      mod.asInstanceOf[Mod]
    }
    else {
      log.warn("No config_class for mod: {}", reference)
      reference
    }
  }

  /**
   * Loads the application configuration from the file system
   */
  protected def makeBuildTimeMod(reference: Mod, modResourceDir: File): Mod = {
    val pFile = new File(modResourceDir, reference.name.get)
    val modFile = new File(pFile, "brzy-module.b.yml")
    val yaml = Yaml(modFile)

    if (yaml.get("config_class").isDefined && yaml.get("config_class").get != null) {
      val configClassName = yaml.get("config_class").get.asInstanceOf[String]
      log.debug("config_class for mod: {}", configClassName)
      val c = Class.forName(configClassName)
      val constructor = c.getConstructor(Array(classOf[Map[_, _]]): _*)
      val modInst = constructor.newInstance(yaml).asInstanceOf[Mod]
      val mod = modInst << reference
      mod.asInstanceOf[Mod]
    }
    else {
//      log.warn("No config_class for mod: {}", reference)
      reference
    }
  }

  def fromJson(json: String) = {
    implicit val formats = Serialization.formats(NoTypeHints)
    val m = parse(json).asInstanceOf[JObject].values
    new WebAppConfig(m("config").asInstanceOf[Map[String, AnyRef]])
  }
}
