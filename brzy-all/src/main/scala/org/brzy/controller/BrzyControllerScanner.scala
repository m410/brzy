package org.brzy.controller

import collection.mutable.ListBuffer
import org.reflections.Reflections
import javax.ws.rs.Path
import org.reflections.util.{ConfigurationBuilder, ClasspathHelper}
import org.reflections.scanners.TypeAnnotationsScanner
import org.brzy.action.Action
import collection.immutable.SortedSet
import collection.immutable.List
import scala.collection.mutable.WrappedArray._
import scala.collection.JavaConversions._


/**
 *   http://code.google.com/p/reflections/
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
@deprecated("Remove Me Later")
class BrzyControllerScanner(val packageName:String) {

  private val reflections = new Reflections(new ConfigurationBuilder()
      .setUrls(ClasspathHelper.getUrlsForPackagePrefix(packageName))
      .setScanners(new TypeAnnotationsScanner()))
  private val controllersInternal:List[Class[_]] = reflections.getTypesAnnotatedWith(classOf[Path]).toList

  def controllers = controllersInternal

  def actionMapping = {
    val list = new ListBuffer[Action]

    controllersInternal.foreach(ctl => {
      val clazz = ctl.asInstanceOf[Class[_<:java.lang.Object]]
      val instance = clazz.newInstance
      val classPath = clazz.getAnnotation(classOf[Path])

      for(method <- clazz.getMethods if method.getAnnotation(classOf[Path]) != null) {
        val methodPath = method.getAnnotation(classOf[Path])
        val pathValue = classPath.value +"/" +  methodPath.value
        list += new Action(pathValue,method, instance, ".jsp") // TODO fix jsp
      }
    })
    SortedSet[Action]() ++ list.toIterable
  }
}

@deprecated("Remove Me Later")
object BrzyControllerScanner {

  def apply(config:String):BrzyControllerScanner = {
    new BrzyControllerScanner(config)
  }
}