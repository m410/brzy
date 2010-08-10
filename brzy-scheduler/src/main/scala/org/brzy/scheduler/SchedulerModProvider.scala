package org.brzy.scheduler

import org.brzy.config.mod.ModProvider
import org.reflections.scanners.{ResourcesScanner, TypeAnnotationsScanner, SubTypesScanner}
import org.reflections.util.{ConfigurationBuilder, ClasspathHelper}
import org.reflections.Reflections
import scala.collection.JavaConversions._

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
class SchedulerModProvider(c: SchedulerModConfig) extends ModProvider {
  val name = c.name.get

  val reflections = new Reflections(new ConfigurationBuilder()
          .setUrls(ClasspathHelper.getUrlsForPackagePrefix("TODO FIX ME"))
          .setScanners(
    new ResourcesScanner(),
    new TypeAnnotationsScanner(),
    new SubTypesScanner()))

  val services = asSet(reflections.getTypesAnnotatedWith(classOf[Cron]))
}