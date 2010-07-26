package org.brzy.mvc.controller


import org.reflections.Reflections
import org.reflections.util.{ConfigurationBuilder, ClasspathHelper}
import org.reflections.scanners.TypeAnnotationsScanner
import collection.immutable.List
import collection.JavaConversions._

/**
 *   http://code.google.com/p/reflections/
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
class ControllerScanner(val packageName:String) {

  private val reflections = new Reflections(new ConfigurationBuilder()
      .setUrls(ClasspathHelper.getUrlsForPackagePrefix(packageName))
      .setScanners(new TypeAnnotationsScanner()))

  val controllers:List[Class[_]] = reflections.getTypesAnnotatedWith(classOf[Controller]).toList
}

object ControllerScanner {
  def apply(config:String) =  new ControllerScanner(config)
}