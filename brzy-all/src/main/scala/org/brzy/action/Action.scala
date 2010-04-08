package org.brzy.action

import java.lang.reflect.Method
import collection.mutable.Buffer
import java.util.regex.Pattern


/**
 * @author Michael Fortin
 * @version $Id: $
 */
class Action(val path:String, val actionMethod:Method, val inst:AnyRef, val viewType:String )
        extends Ordered[Action] {

  val returnTypes =  actionMethod.getReturnType

  val parameterTypes = actionMethod.getParameterTypes

  val defaultView = {
    val clazz = inst.getClass
    val folder =
      if(clazz.getSimpleName.indexOf("Controller") == -1)
        clazz.getSimpleName
      else
        clazz.getSimpleName.substring(0,clazz.getSimpleName.indexOf("Controller"))
    // TODO should be configurable
    "/" + folder.toLowerCase + "/"+ actionMethod.getName + viewType
  }


  private val strPattern = "^" + path.replaceAll("""\{.*?\}""","""(.*?)""") + "$"
  val pattern = strPattern.r

  def matchParameters(url:String) = {
    val buffer = Buffer[String]()
    val matcher = Pattern.compile(strPattern).matcher(url)
    if(matcher.find)
      for(i <- 1 to matcher.groupCount)
        buffer += matcher.group(i)
    buffer.toArray
  }

  val matchParameterIds = {
    val buffer = Buffer[String]()
    val matcher = Pattern.compile("""\{(.*?)\}""").matcher(path)
    while(matcher.find)
        buffer += matcher.group(1)
    buffer.toArray
  }

  override def compare(that : Action) : Int =  path.compareToIgnoreCase(that.path)

  override def toString = "Action('" + path + "', " + actionMethod + ")"
}
