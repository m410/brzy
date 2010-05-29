package org.brzy.config

import java.util.{Map => JMap, List => JList, HashMap => JHashMap, ArrayList => JArrayList}
import collection.JavaConversions._
import org.slf4j.LoggerFactory
import org.brzy.util.UrlUtils._
import org.brzy.util.FileUtils._
import org.ho.yaml.Yaml
import java.io.{InputStream, File}
import org.brzy.util.NestedCollectionConverter._


/**
 * Document Me..
 *
 * @author Michael Fortin
 * @version $Id : $
 */
class BootConfigBuilder(appFile: File, environment: String) {
  private val log = LoggerFactory.getLogger(getClass)
  private val dev = "development"
  private val prod = "production"
  private val test = "test"

  assert(appFile != null, "configuration file is null")
  assert(appFile.exists, "configuration file doesn not exist: " + appFile.getAbsolutePath)


  private val webappConfigMap: Map[String, AnyRef] = {
    val load = Yaml.load(appFile).asInstanceOf[JMap[String, AnyRef]]
    convertMap(load)
  }
  private val defaultConfigMap: Map[String, AnyRef] = {
    val asStream: InputStream = getClass.getClassLoader.getResourceAsStream("brzy-webapp.default.b.yml")
    val load = Yaml.load(asStream).asInstanceOf[JMap[String, AnyRef]]
    convertMap(load)
  }

  val applicationConfig: WebappConfig = {
    new WebappConfig(webappConfigMap ++ Map[String, String]("environment" -> environment))
  }

  val defaultConfig: WebappConfig = {
    new WebappConfig(defaultConfigMap)
  }

  val environmentConfig: WebappConfig = {
    val list = webappConfigMap.get("environment_overrides").get.asInstanceOf[List[Map[String, AnyRef]]]
    val option = list.find(innermap => {
      val tuple = innermap.find(hm => hm._1 == "environment").get
      tuple._2.asInstanceOf[String].compareTo(environment) == 0
    })

    if (option.isDefined)
      new WebappConfig(option.get.asInstanceOf[Map[String, AnyRef]])
    else
      new WebappConfig(Map[String,AnyRef]())
  }

  val config = defaultConfig << applicationConfig << environmentConfig

  def to(map: Map[String, AnyRef], jm: JMap[String, AnyRef]): Unit = {
    map.foreach(nvp => {
      nvp._2 match {
        case None =>
        case s: Some[_] =>
          if (s.get != null && s.get.isInstanceOf[String])
            jm.put(nvp._1, s.get.asInstanceOf[String])
          else if (s.get != null && s.get.isInstanceOf[List[_]]) {
            val list = new JArrayList[AnyRef]()
            to(s.get.asInstanceOf[List[AnyRef]], list)
            jm.put(nvp._1, list)
          }
          else if (s.get != null && s.get.isInstanceOf[Map[_, _]]) {
            val map = new JHashMap[String, AnyRef]()
            to(s.get.asInstanceOf[Map[String, AnyRef]], map)
            jm.put(nvp._1, map)
          }
        case m: Map[String, AnyRef] =>
          val map = new JHashMap[String, AnyRef]()
          to(m, map)
          jm.put(nvp._1, map)
        case l: List[AnyRef] =>
          val list = new JArrayList[AnyRef]()
          to(l, list)
          jm.put(nvp._1, list)
        case _ =>
          jm.put(nvp._1, nvp._2)
      }
    })
  }

  def to(slist: List[AnyRef], jlist: JList[AnyRef]): Unit = {
    slist.foreach(entry => {
      entry match {
        case None =>
        case s: Some[_] =>
          if (s.get != null && s.get.isInstanceOf[String])
            jlist.add(s.get.asInstanceOf[String])
          else if (s.get != null && s.get.isInstanceOf[List[_]]) {
            val list = new JArrayList[AnyRef]()
            to(s.get.asInstanceOf[List[AnyRef]], list)
            jlist.add(list)
          }
          else if (s.get != null && s.get.isInstanceOf[Map[_, _]]) {
            val map = new JHashMap[String, AnyRef]()
            to(entry.asInstanceOf[Map[String, AnyRef]], map)
            jlist.add(map)
          }
        case m: Map[String, AnyRef] =>
          val map = new JHashMap[String, AnyRef]()
          to(m, map)
          jlist.add(map)
        case l: List[AnyRef] =>
          val list = new JArrayList[AnyRef]()
          to(l, list)
          jlist.add(list)
        case t: (String, String) =>
          val map = new JHashMap[String, AnyRef]()
          if (t._2.isInstanceOf[List[_]])
            map.put(t._1, new JArrayList())
          else
            map.put(t._1, t._2)
          jlist.add(map)
        case _ =>
          jlist.add(entry)
      }
    })
  }

  def writeMerged(yamlFile: File) = {
    val map = new JHashMap[String, AnyRef]()
    to(config.asMap, map)
    val DS = System.getProperty("file.separator")

    Yaml.dump(map, yamlFile, true)
  }
}

object BootConfigBuilder {
  def apply(appFile: File, environment: String) = new BootConfigBuilder(appFile, environment)
}