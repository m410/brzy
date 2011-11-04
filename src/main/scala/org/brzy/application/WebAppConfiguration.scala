package org.brzy.application

import java.io.{File, PrintWriter}
import org.slf4j.LoggerFactory
import com.twitter.json.Json

import org.brzy.fab.mod.{Mod, ProjectModuleConfiguration}
import org.brzy.fab.conf.{Yaml, Logging}

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
class WebAppConfiguration(override val map: Map[String, AnyRef]) extends ProjectModuleConfiguration(map) {

  val useSsl: Option[Boolean] = map.get("use_ssl") match {
    case Some(e) => if(e != null) Option(e.asInstanceOf[Boolean]) else Option(false)
    case _ => Option(false)
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
    pw.println("Use SSL: " + useSsl.getOrElse("<None>"))
    pw.println("Logging")
    logging match {
      case Some(l) => l.prettyPrint(tab,pw)
      case _ => pw.println("<None>")
    }

    pw.println("web.xml")
    webXml match {
      case Some(l) => pw.println(tab + l)
      case _ => pw.println("<None>")
    }
  }


  override protected[this] def merge(t: ProjectModuleConfiguration) = {
    val that = t.asInstanceOf[WebAppConfiguration]
    instance(Map[String, AnyRef](
        "use_ssl" -> this.useSsl.getOrElse(that.useSsl.orNull),
        "logging" -> {
          if (this.logging.isDefined)
            {this.logging.get << that.logging.orNull}.map
          else if (that.logging.isDefined)
            that.logging.get.map
          else
            None
        },
        "web_xml" -> {this.webXml ++ that.webXml}
    ) ++ super.<<(that).map)
  }

  override protected[application] def instance(m:Map[String,AnyRef]) = {
    new WebAppConfiguration(map)
  }
}

object WebAppConfiguration {

  private val log = LoggerFactory.getLogger(getClass)

  val defaultConfigFile = "/brzy-webapp.default.b.yml"

  val appConfigFile = "/brzy-webapp.b.yml"

  def runtime(env: String, appConfig: String = appConfigFile, defaultConfig: String = defaultConfigFile) = {
    val defaultConf = new WebAppConfiguration(Yaml(getClass.getResourceAsStream(defaultConfig)))
    val appConf = new WebAppConfiguration(Yaml(getClass.getResourceAsStream(appConfig)))
    val envConfig = appConf.map.get("environment_overrides") match {
      case Some(ec) => new WebAppConfiguration(ec.asInstanceOf[Map[String,AnyRef]])
      case _ => new WebAppConfiguration(Map.empty[String,AnyRef])
    }
    val viewModule = makeRuntimeMod(appConf.views.get)
    val persistenceModules = envConfig.persistence.map(makeRuntimeMod(_))
    val modules = envConfig.modules.map(makeRuntimeMod(_))
    val m1 = defaultConf << appConf << viewModule
    val m2 = persistenceModules.foldLeft(m1)((r,c) => r << c)
    val m3 = modules.foldLeft(m2)((r,c) => r << c)
    val m4 = m3 << envConfig
    m4.asInstanceOf[WebAppConfiguration]
  }

  def buildtime(modBaseDir: File, env: String, appConfigPath: String, defaultConfig: String = defaultConfigFile) = {
    val defaultConf = new WebAppConfiguration(Yaml(getClass.getResourceAsStream(defaultConfig)))
    val appConf = new WebAppConfiguration(Yaml(new File(appConfigPath)))
    val envConfig = appConf.map.get("environment_overrides") match {
      case Some(ec) => new WebAppConfiguration(ec.asInstanceOf[Map[String,AnyRef]])
      case _ => new WebAppConfiguration(Map.empty[String,AnyRef])
    }
    val viewModule = makeBuildTimeMod(appConf.views.get,modBaseDir)
    val persistenceModules = envConfig.persistence.map(makeBuildTimeMod(_,modBaseDir))
    val modules = envConfig.modules.map(makeBuildTimeMod(_,modBaseDir))
    val m1 = defaultConf << appConf << viewModule
    val m2 = persistenceModules.foldLeft(m1)((r,c) => r << c)
    val m3 = modules.foldLeft(m2)((r,c) => r << c)
    val m4 = m3 << envConfig
    m4.asInstanceOf[WebAppConfiguration]
  }


  /**
   * Loads the application configuration from the classpath
   */
  protected[application] def makeRuntimeMod(reference: Mod): Mod = {
    val modResource: String = "modules/" + reference.name.get + "/brzy-module.b.yml"
    log.debug("module conf: '{}'", modResource)
    val cpUrl = getClass.getClassLoader.getResource(modResource)
    val yaml = Yaml(cpUrl.openStream)

    if (yaml.get("config_class").isDefined && yaml.get("config_class").get != null) {
      // this needs to be in this class, because of classloader scope issues.  Same
      // with the duplicate code below.  This should remove this later once the build runner
      // classloader is ironed out.
      val c = Class.forName(yaml.get("config_class").get.asInstanceOf[String])
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
  protected[application] def makeBuildTimeMod(reference: Mod, modResourceDir: File): Mod = {
    val pFile = new File(modResourceDir, reference.name.get)
    val modFile = new File(pFile, "brzy-module.b.yml")
    val yaml = Yaml(modFile)

    if (yaml.get("config_class").isDefined && yaml.get("config_class").get != null) {
      val c = Class.forName(yaml.get("config_class").get.asInstanceOf[String])
      val constructor = c.getConstructor(Array(classOf[Map[_, _]]): _*)
      val modInst = constructor.newInstance(yaml).asInstanceOf[Mod]
      val mod = modInst << reference
      mod.asInstanceOf[Mod]
    }
    else {
      reference
    }
  }

  def fromJson(json: String) = {
    new WebAppConfiguration(Json.parse(json).asInstanceOf[Map[String, AnyRef]])
  }
}