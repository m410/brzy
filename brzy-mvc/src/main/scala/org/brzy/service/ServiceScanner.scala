package org.brzy.service

import org.reflections.Reflections
import org.reflections.util.{ConfigurationBuilder, ClasspathHelper}
import org.reflections.scanners.{ResourcesScanner, TypeAnnotationsScanner, SubTypesScanner}

import scala.collection.JavaConversions._

/**
 * @author Michael Fortin
 */
class ServiceScanner(val packageName:String) {

  val reflections = new Reflections(new ConfigurationBuilder()
        .setUrls(ClasspathHelper.getUrlsForPackagePrefix(packageName))
        .setScanners(
            new ResourcesScanner(),
            new TypeAnnotationsScanner(),
            new SubTypesScanner()))

  val services = asSet(reflections.getTypesAnnotatedWith(classOf[Service]))
}

object ServiceScanner {
  def apply(config:String) = new ServiceScanner(config)
}