package org.brzy.scheduler

import org.brzy.config.mod.ModProvider
import org.reflections.scanners.{ResourcesScanner, TypeAnnotationsScanner, SubTypesScanner}
import org.reflections.util.{ConfigurationBuilder, ClasspathHelper}
import org.reflections.Reflections
import collection.JavaConversions._
import collection.mutable.{ListBuffer, HashMap}

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
class SchedulerModProvider(c: SchedulerModConfig) extends ModProvider {
  val name = c.name.get

  private val reflections = new Reflections(new ConfigurationBuilder()
          .setUrls(ClasspathHelper.getUrlsForPackagePrefix(c.scanPackage.get))
          .setScanners(
    new ResourcesScanner(),
    new TypeAnnotationsScanner(),
    new SubTypesScanner()))

  val jobs = {
    val services = asSet(reflections.getTypesAnnotatedWith(classOf[Cron]))
    val list = ListBuffer[Schedule]()
    services.foreach(s=> {
      val instance = s.newInstance.asInstanceOf[AnyRef]
      val annotation = s.getAnnotation(classOf[Cron])
      list += Schedule(new JobRunner(instance, null),annotation.value)
    })
    list.toList
  }



  override val serviceMap = {
    val map = HashMap[String, AnyRef]()
    jobs.foreach(job=>  map += job.serviceName -> job.service )
    map.toMap
  }

  override def startup = jobs.foreach(_.start)

  override def shutdown = jobs.foreach(_.stop)

}