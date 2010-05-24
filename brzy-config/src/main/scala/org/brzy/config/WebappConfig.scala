package org.brzy.config

import org.brzy.plugin.Plugin
import java.lang.reflect.Constructor
import java.lang.String
import collection.mutable.ListBuffer

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
  val configurationName: String = "Application Configuration"
  val environment: Option[String] = m.get("environment").asInstanceOf[Option[String]].orElse(None)
  val testFramework: Option[String] = m.get("test_framework").asInstanceOf[Option[String]].orElse(None)
  val application: Option[Application] = m.get("application") match {
    case s: Some[_] => Option(new Application(s.get.asInstanceOf[Map[String, String]]))
    case _ => None
  }
  val project: Option[Project] = m.get("project") match {
    case s: Some[_] => Option(new Project(s.get.asInstanceOf[Map[String, String]]))
    case _ => None
  }
  val logging: Option[Logging] = m.get("logging") match {
    case s: Some[_] => Option(new Logging(s.get.asInstanceOf[Map[String, AnyRef]]))
    case _ => None
  }
  val webXml: Option[List[Map[String, AnyRef]]] = m.get("web_xml").asInstanceOf[Option[List[Map[String, AnyRef]]]]
  val views: Option[Plugin] = m.get("views") match {
    case s: Some[Map[String, AnyRef]] =>
      val str: String = s.get.get("config_class").get.asInstanceOf[String]
      val clazz = Class.forName(str)
      val constructor = clazz.getConstructor(classOf[Map[String, AnyRef]])
      Option(constructor.newInstance(s.get).asInstanceOf[Plugin])
    case _ => None
  }

  val repositories: Option[List[Repository]] = m.get("repositories") match {
    case s: Some[List[Map[String, AnyRef]]] => Option(s.get.map(i => new Repository(i)).toList)
    case _ => None
  }
  val dependencies: Option[List[Dependency]] = m.get("dependencies") match {
    case s: Some[List[Map[String, AnyRef]]] => Option(s.get.map(i => new Dependency(i)).toList)
    case _ => None
  }
  val plugins: Option[List[Plugin]] = m.get("plugins") match {
    case s: Some[List[Map[String, AnyRef]]] =>
      val buffer = new ListBuffer[Plugin]()
      s.get.foreach(map => {
        val className = map.get("config_class")
        val clazz = Class.forName(className.get.asInstanceOf[String])
        val constructor: Constructor[_] = clazz.getConstructor(classOf[Map[String, AnyRef]])
        buffer += constructor.newInstance(map).asInstanceOf[Plugin]
      })
      Option(buffer.toList)
    case _ => None
  }
  val persistence: Option[List[Plugin]] = m.get("persistence") match {
    case s: Some[List[Map[String, AnyRef]]] =>
      val buffer = new ListBuffer[Plugin]()
      s.get.foreach(map => {
        val className = map.get("config_class")
        val clazz = Class.forName(className.get.asInstanceOf[String])
        val constructor: Constructor[_] = clazz.getConstructor(classOf[Map[String, AnyRef]])
        buffer += constructor.newInstance(map).asInstanceOf[Plugin]
      })
      Option(buffer.toList)
    case _ => None
  }

  def asMap = {
    Map[String, AnyRef](
      "environment" -> environment,
      "application" -> {application match {
        case s: Some[Application] => s.get.asMap
        case _ => null
      }},
      "project" -> {project match {
        case s: Some[Project] => s.get.asMap
        case _ => null
      }},
      "test_framework" -> testFramework,
      "repositories" -> { repositories match {
        case s: Some[List[Repository]] => s.get.map(_.asMap.toList)
        case _ => null
      }},
      "dependencies" -> {dependencies match {
        case s: Some[List[Dependency]] => s.get.map(_.asMap).toList
        case _ => null
      }},
      "logging" -> {logging match {
        case s: Some[Logging] => s.get.asMap
        case _ => null
      }},
      "plugins" -> { plugins match {
        case s: Some[List[Plugin]] => s.get.map(_.asMap.toList)
        case _ => null
      }},
      "persistence" -> {persistence match {
        case s: Some[List[Plugin]] => s.get.map(_.asMap.toList)
        case _ => null
      }},
      "web_xml" -> webXml,
      "views" -> {views match {
        case s: Some[Plugin] => s.get.asMap
        case _ => null
      }})
  }

  /**
   * merge this with other config, and return a new one
   */
  def <<(that: WebappConfig) = {

    if (that == null) {
      this
    }
    else {
      new WebappConfig(Map[String, AnyRef](
        "environment" -> this.environment.getOrElse(that.environment.get),
        "application" -> {this.application.getOrElse(that.application.get)}.asMap,
        "project" -> {this.project.get << that.project.getOrElse(null)}.asMap,
        "repositories" -> {
          if (this.repositories.isDefined && that.repositories.isDefined)
            this.repositories.get.map(_.asMap).toList ++ that.repositories.get.map(_.asMap).toList
          else if (this.repositories.isDefined)
            this.repositories.get.map(_.asMap).toList
          else if (that.repositories.isDefined)
            that.repositories.get.map(_.asMap).toList
          else
            null
        },
        "dependencies" -> {
          if (this.dependencies.isDefined && that.dependencies.isDefined)
            this.dependencies.get.map(_.asMap).toList ++ that.dependencies.get.map(_.asMap).toList
          else if (this.dependencies.isDefined)
            this.dependencies.get.map(_.asMap).toList
          else if (that.dependencies.isDefined)
            that.dependencies.get.map(_.asMap).toList
          else
            null
        },
        "views" -> {
          if (this.views.isDefined && that.views.isDefined)
            {this.views.get << that.views.get}.asMap
          else if (this.views.isDefined)
            this.views.get.asMap
          else if (that.views.isDefined)
            that.views.get.asMap
          else
            null
        },
        "persistence" -> {
          if (this.persistence.isDefined && that.persistence.isDefined)
            {this.persistence.get ++ that.persistence.get}.map(_.asMap).toList
          else if (this.persistence.isDefined)
            this.persistence.get.map(_.asMap).toList
          else if (that.persistence.isDefined)
            that.persistence.get.map(_.asMap).toList
          else
            null
        },
        "plugins" -> {
          if (this.plugins.isDefined && that.plugins.isDefined)
            {this.plugins.get ++ that.plugins.get}.map(_.asMap).toList
          else if (this.plugins.isDefined)
            this.plugins.get.map(_.asMap).toList
          else if (that.plugins.isDefined)
            that.plugins.get.map(_.asMap).toList
          else
            null
        },
        "web_xml" -> {
          if (this.webXml.isDefined && that.webXml.isDefined)
            this.webXml.get ++ that.webXml.get
          else if (this.webXml.isDefined)
            this.webXml.get
          else if (that.webXml.isDefined)
            that.webXml.get
          else
            None
        },
        "logging" -> {
          if (this.logging.isDefined )
            {this.logging.get << that.logging.getOrElse(null)}.asMap
          else if (that.logging.isDefined)
            that.logging.get.asMap
          else
            None
        }
        ))
    }
  }
}