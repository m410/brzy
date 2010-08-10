package org.brzy.scheduler

import org.brzy.config.mod.ModProvider
import org.reflections.scanners.{ResourcesScanner, TypeAnnotationsScanner, SubTypesScanner}
import org.reflections.util.{ConfigurationBuilder, ClasspathHelper}
import org.reflections.Reflections
import scala.collection.JavaConversions._
import collection.mutable.HashMap

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
class SchedulerModProvider(c: SchedulerModConfig) extends ModProvider {
  val name = c.name.get

  val reflections = new Reflections(new ConfigurationBuilder()
          .setUrls(ClasspathHelper.getUrlsForPackagePrefix(c.scanPackage.get))
          .setScanners(
    new ResourcesScanner(),
    new TypeAnnotationsScanner(),
    new SubTypesScanner()))

  override val serviceMap = {
    val services = asSet(reflections.getTypesAnnotatedWith(classOf[Cron]))
    val map = HashMap[String, AnyRef]()
    services.foreach(s=> {
      val in = s.getClass.getName
      map += (in.charAt(0).toLower + in.substring(1,in.length) -> s)
    })
    map.toMap
  }

  override def shutdown = {
    // todo init the scheduler
  }

  override def startup = {
    // todo shutdown and threads in the scheduler
  }
}