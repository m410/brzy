package org.brzy.config


import java.util.{Map => JMap, List => JList}
import collection.JavaConversions._
import org.brzy.plugin.Plugin
import collection.Seq
import collection.mutable.{Buffer, ArrayBuffer}

/**
 * load default, load plugins, load app.
 * loading default and plugins has to ignore the about tag.
 *
 * implicit plugins:
 * logging, persistence
 *
 * @author Michael Fortin
 * @version $Id : $
 */
class WebappConfig(m: Map[String, AnyRef]) extends Config(m) with MergeConfig[WebappConfig] {
  private val dev = "developement"
  private val prod = "production"
  private val test = "test"

  val environment = set[String](m.get("environment"))
  val application = make[Application](classOf[Application], m.get("application"))

  val project = make[Project](classOf[Project], m.get("project"))

  val testFramework: String = set[String](m.get("test_framework"))

  val repositories = makeSeq[Repository](classOf[Repository], m.get("repositories"))
  val dependencies = makeSeq[Dependency](classOf[Dependency], m.get("dependencies"))

  val logging = make[Logging](classOf[Logging], m.get("logging"))
  val plugins = makePluginList(m.get("plugins"))
  val persistence = makePluginList(m.get("persistence"))
  val webXml = makeWebXml(m.get("web_xml"))
  val views: Plugin[_] = makePlugin(m.get("views"))

  val configurationName = "Application Configuration"

  def asMap = {
    val map = new collection.mutable.HashMap[String, AnyRef]()

    if (environment != null)
      map.put("environment", environment)

    if (application != null)
      map.put("application", application.asMap)

    if (project != null)
      map.put("project", project.asMap)

    map.put("test_framework", testFramework)

    if (repositories != null)
      map.put("repositories", repositories.map(m => m.asMap))

    if (dependencies != null)
      map.put("dependencies", dependencies.map(m => m.asMap))

    if (logging != null)
      map.put("logging", logging.asMap)

    if (plugins != null)
      map.put("plugins", plugins.map(m => m.asInstanceOf[Plugin[_]].asMap))

    if (persistence != null)
      map.put("persistence", persistence.map(m => m.asInstanceOf[Plugin[_]].asMap))

    if (webXml != null)
      map.put("web_xml", webXml)

    if (views != null)
      map.put("views", views.asInstanceOf[Plugin[_]].asMap)

    Map[String, AnyRef]() ++ map
  }

  /**
   * merge this with other config, and return a new one
   */
  def +(that: WebappConfig) = {
    val map = new collection.mutable.HashMap[String, AnyRef]()
    map.put("application", this.application.asMap)
    map.put("project", (that.project + this.project).asMap)

    if (this.views != null)
      map.put("views", this.views.asMap)
    else
      map.put("views", that.views.asMap)

    val seq = this.repositories ++ that.repositories
    map.put("repositories", seq.map(m => m.asMap))
    val seq1 = this.dependencies ++ that.dependencies
    map.put("dependencies", seq1.map(m => m.asMap))


    val buffer =
        if (this.persistence != null && that.persistence != null)
          this.persistence ++ that.persistence
        else if (this.persistence != null)
          this.persistence
        else if (that.persistence != null)
          that.persistence

    map.put("persistence", buffer.asInstanceOf[Buffer[Plugin[_]]].map(m => m.asMap))

    map.put("plugins", this.plugins.map(m => m.asInstanceOf[Plugin[_]].asMap))

    map.put("web_xml", this.webXml ++ that.webXml)

    new WebappConfig(this.asMap ++ map)
  }

  //  def ++(thosePlugins:Array[PluginConfig]) = {
  //    if(plugins == null) {
  //      plugins = thosePlugins
  //    }
  //    else {
  //      thosePlugins.foreach(thatPlugin =>
  //        if(plugins.exists(thisPlugin => thisPlugin.name == thatPlugin.name)) {
  //          var thisPlugin = plugins.find(thisPlugin => thisPlugin.name == thatPlugin.name).get
  //          thisPlugin = thisPlugin + thatPlugin
  //        }
  //        else {
  //          plugins = plugins :+ thatPlugin
  //        }
  //      )
  //    }
  //
  //    plugins.foreach(p => {
  //
  //      if(p.web_xml != null)
  //        this.webXml.addAll(p.web_xml)
  //
  //      if(p.dependencies != null)
  //        this.dependencies = this.dependencies ++ p.dependencies
  //
  //      if(p.repositories != null)
  //        this.repositories = this.repositories ++ p.repositories
  //    })
  //
  //    this
  //  }

}