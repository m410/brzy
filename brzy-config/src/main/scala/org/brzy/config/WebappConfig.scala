package org.brzy.config


import java.util.{Map=>JMap,List=>JList}
import collection.JavaConversions._

/**
 * load default, load plugins, load app.
 * loading default and plugins has to ignore the about tag.
 *
 * implicit plugins:
 * logging, persistence
 *
 * @author Michael Fortin
 * @version $Id: $
 */
class WebappConfig(m:Map[String,AnyRef]) extends Config(m) with MergeConfig[WebappConfig] {

  private val dev = "developement"
  private val prod = "production"
  private val test = "test"

  val environment = set[String](m.get("environment"))
  val application = make[Application](classOf[Application], m.get("application"))

  val project = make[Project](classOf[Project], m.get("project"))

  val testFramework:String = set[String](m.get("test_framework"))

  val repositories = makeSeq[Repository](classOf[Repository], m.get("repositories"))
  val dependencies = makeSeq[Dependency](classOf[Dependency], m.get("dependencies"))

  val logging = make[Logging](classOf[Logging], m.get("logging"))
  val plugins = makePluginList(m.get("plugins"))
  val persistence = makePluginList(m.get("persistence"))
  val webXml = makeWebXml(m.get("web_xml"))
  val views = makePlugin(m.get("views"))

  val configurationName = "Application Configuration"

  def asMap = {
    val map = Map[String,AnyRef]()
    // TODO add each property
    map
  }

	/**
	 * merge this with other config, and return a new one
	 */
  def +(that:WebappConfig) =  new WebappConfig(this.asMap ++ that.asMap)


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