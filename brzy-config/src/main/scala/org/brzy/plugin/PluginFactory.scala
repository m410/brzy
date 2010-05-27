package org.brzy.webapp

import org.brzy.plugin.Plugin
import org.brzy.config.WebappConfig
import java.lang.reflect.Constructor

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */

object PluginFactory {
  
  def make(config:WebappConfig, yaml:Map[String,AnyRef]):Plugin ={
    val configClass: String = yaml.get("config_class").get.asInstanceOf[String]
    val pluginClass = Class.forName(configClass).asInstanceOf[Class[_]]
    val constructor: Constructor[_] = pluginClass.getConstructor(classOf[Map[String, AnyRef]])
    val newPluginInstance = constructor.newInstance(yaml)
    newPluginInstance.asInstanceOf[Plugin]
  }
}