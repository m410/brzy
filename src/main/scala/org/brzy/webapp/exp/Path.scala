package org.brzy.webapp.exp

import collection.mutable.Buffer
import java.util.regex.Pattern

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
case class Path(base:String, sub:String) {

  protected[exp] val path = base + sub
  protected[exp] val strPattern = "^/*" + path.replaceAll("""\{.*?\}""", """(.*?)""") + "$"
  protected[exp] val pattern = strPattern.r

  def isMatch(contextPath:String) = {
    val actionPrePath =
        if (path.startsWith("/"))
          path
        else
          "/" + path

     val actionTokens:Array[String] = actionPrePath.replaceAll("/+", "/").split("/")
     val urlTokens:Array[String] = contextPath.replaceAll("/+", "/").split("/")

     if(actionTokens.size == urlTokens.size){
       var map = Map[String,String]()

       for (x <- 0 to urlTokens.size -1)
         map += urlTokens(x) -> actionTokens(x)

       map.forall((nvp)=> nvp._1 == nvp._2 || nvp._2.startsWith("{"))
     }
     else
       false
  }

  def extractParameterValues(contextPath:String) = {
    val buffer = Buffer[String]()
    val matcher = Pattern.compile(strPattern).matcher(contextPath)

    if (matcher.find)
      for (i <- 1 to matcher.groupCount)
        buffer += matcher.group(i)

    buffer.toArray
  }

  val parameterNames = {
    val buffer = Buffer[String]()
    val matcher = Pattern.compile("""\{(.*?)\}""").matcher(path)

    while (matcher.find)
      buffer += matcher.group(1)

    buffer.toArray
  }
}