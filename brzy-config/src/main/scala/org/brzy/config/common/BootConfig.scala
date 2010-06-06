package org.brzy.config.common

import org.brzy.config.plugin.Plugin
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
class BootConfig(m: Map[String, AnyRef]) extends Config(m) with MergeConfig[BootConfig] {
  private val dev = "developement"
  private val prod = "production"
  private val test = "test"
  val configurationName: String = "Boot Configuration"
  val environment: Option[String] = m.get("environment").asInstanceOf[Option[String]].orElse(None)
  val testFramework: Option[String] = m.get("test_framework").asInstanceOf[Option[String]].orElse(None)
  val application: Option[Application] = m.get("application") match {
    case s: Some[_] => Option(new Application(s.get.asInstanceOf[Map[String, String]]))
    case _ => None
  }
  val project: Option[Project] = m.get("project") match {
    case Some(s) =>
      if (s != null)
        Option(new Project(s.asInstanceOf[Map[String, String]]))
      else
        None
    case _ => None
  }
  val logging: Option[Logging] = m.get("logging") match {
    case Some(s) =>
      if (s != null)
        Option(new Logging(s.asInstanceOf[Map[String, AnyRef]]))
      else
        None
    case _ => None
  }
  val webXml: Option[List[Map[String, AnyRef]]] = m.get("web_xml") match {
    case Some(s) =>
      if (s != null && s.isInstanceOf[List[_]])
        Option(s.asInstanceOf[List[Map[String, AnyRef]]])
      else
        None
    case _ => None
  }

  val views: Option[Plugin] = m.get("views") match {
    case Some(s) =>
      if (s != null)
        Option(new Plugin(s.asInstanceOf[Map[String, AnyRef]]))
      else
        None
    case _ => None
  }

  val repositories: Option[List[Repository]] = m.get("repositories") match {
    case Some(s) =>
      if (s != null)
        Option(s.asInstanceOf[List[Map[String, AnyRef]]].map(i => new Repository(i)).toList)
      else
        None
    case _ => None
  }
  val dependencies: Option[List[Dependency]] = m.get("dependencies") match {
    case Some(s) =>
      if (s != null)
        Option(s.asInstanceOf[List[Map[String, AnyRef]]].map(i => new Dependency(i)).toList)
      else
        None
    case _ => None
  }
  val plugins: Option[List[Plugin]] = m.get("plugins") match {
    case Some(s) =>
      if (s != null) {
        val buffer = new ListBuffer[Plugin]()
        s.asInstanceOf[List[Map[String, AnyRef]]].foreach(map => {
          buffer += new Plugin(map)
        })
        Option(buffer.toList)
      }
      else
        None
    case _ => None
  }
  val persistence: Option[List[Plugin]] = m.get("persistence") match {
    case Some(s) =>
      val buffer = new ListBuffer[Plugin]()
      if (s != null) {
        s.asInstanceOf[List[Map[String, AnyRef]]].foreach(map => {
          buffer += new Plugin(map)
        })
        Option(buffer.toList)
      }
      else {
        None
      }
    case _ => None
  }

  def asMap = {
    Map[String, AnyRef](
      "environment" -> environment,
      "application" -> {
        application match {
          case Some(a) => a.asInstanceOf[Application].asMap
          case _ => null
        }
      },
      "project" -> {
        project match {
          case Some(p) => p.asInstanceOf[Project].asMap
          case _ => null
        }
      },
      "views" -> {
        views match {
          case Some(v) => v.asMap
          case _ => null
        }
      },
      "test_framework" -> testFramework,
      "repositories" -> {
        repositories match {
          case Some(a) => a.asInstanceOf[List[Repository]].map(_.asMap)
          case _ => null
        }
      },
      "dependencies" -> {
        dependencies match {
          case Some(a) => a.asInstanceOf[List[Dependency]].map(_.asMap)
          case _ => null
        }
      },
      "logging" -> {
        logging match {
          case Some(a) => a.asInstanceOf[Logging].asMap
          case _ => null
        }
      },
      "plugins" -> {
        plugins match {
          case Some(a) => a.asInstanceOf[List[Plugin]].map(_.asMap)
          case _ => null
        }
      },
      "persistence" -> {
        persistence match {
          case Some(a) => a.asInstanceOf[List[Plugin]].map(_.asMap)
          case _ => null
        }
      },
      "web_xml" -> {
        webXml match {
          case Some(a) => a.asInstanceOf[List[Map[String, AnyRef]]]
          case _ => null
        }
      },
      "views" -> {
        views match {
          case Some(a) => a.asInstanceOf[Plugin].asMap
          case _ => null
        }
      })
  }

  /**
   * merge this with other config, and return a new one
   */
  def <<(that: BootConfig) = {

    if (that == null) {
      this
    }
    else {
      new BootConfig(Map[String, AnyRef](
        "environment" -> this.environment.getOrElse(that.environment.get),
        "application" -> {this.application.getOrElse(that.application.get)}.asMap,
        "project" -> {
          if (this.project.isDefined && this.project.get != null)
            {this.project.get << that.project.getOrElse(null)}.asMap
          else if (that.project.isDefined && that.project.get != null)
            that.project.get.asMap
          else
            null
        },
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
          if (this.views.isDefined && this.views.get != null)
            {this.views.get << that.views.getOrElse(null)}.asMap
          else if (that.views.isDefined)
            that.views.get.asMap
          else
            null
        },
        "persistence" -> {
          if (this.persistence.isDefined && that.persistence.isDefined)
            {this.persistence.get ++ that.persistence.get}.distinct.map(_.asMap).toList
          else if (this.persistence.isDefined)
            this.persistence.get.distinct.map(_.asMap).toList
          else if (that.persistence.isDefined)
            that.persistence.get.distinct.map(_.asMap).toList
          else
            null
        },
        "plugins" -> {
          if (this.plugins.isDefined && that.plugins.isDefined)
            {this.plugins.get ++ that.plugins.get}.distinct.map(_.asMap).toList
          else if (this.plugins.isDefined)
            this.plugins.get.distinct.map(_.asMap).toList
          else if (that.plugins.isDefined)
            that.plugins.get.distinct.map(_.asMap).toList
          else
            null
        },
        "web_xml" -> {
          println("this: " + this.webXml)
          println("that: " + that.webXml)
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
          if (this.logging.isDefined)
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