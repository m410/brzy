package org.brzy.service

import org.reflections.Reflections
import org.reflections.util.{ConfigurationBuilder, ClasspathHelper}
import org.reflections.scanners.{ResourcesScanner, TypeAnnotationsScanner, SubTypesScanner}

import scala.collection.JavaConversions._

/**
 * @author Michael Fortin
 * @version $Id: $
 */
@deprecated("Remove Me Later")
class BrzyServiceScanner(val packageName:String) {

  val reflections = new Reflections(new ConfigurationBuilder()
        .setUrls(ClasspathHelper.getUrlsForPackagePrefix(packageName))
        .setScanners(
            new ResourcesScanner(),
            new TypeAnnotationsScanner(),
            new SubTypesScanner()))

  val serviceAnnotationClass = classOf[Service]
  val serviceInternal = asSet(reflections.getTypesAnnotatedWith(serviceAnnotationClass))

  val service1AnnotationClass = classOf[ScheduledService]
  val service1Internal = asSet(reflections.getTypesAnnotatedWith(service1AnnotationClass))

  val service2AnnotationClass = classOf[MessageService]
  val service2Internal = asSet(reflections.getTypesAnnotatedWith(service2AnnotationClass))

  val services =  serviceInternal :: service1Internal :: service2Internal :: Nil
}

@deprecated("Remove Me Later")
object BrzyServiceScanner {

  def apply(config:String):BrzyServiceScanner = {
    new BrzyServiceScanner(config)
  }
}