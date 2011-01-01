package org.brzy.webapp.controller

import collection.mutable.Buffer
import java.util.regex.Pattern
import org.slf4j.LoggerFactory

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
case class Path(base: String, sub: String) extends Ordered[Path] {

  protected[controller] val log = LoggerFactory.getLogger(classOf[Path])

  protected[controller] val path = {
    val combined =
      if (!sub.startsWith("/"))
        base + "/" + sub
      else
        base + sub

    val preSlash =
      if (!combined.startsWith("/"))
        "/" + combined
      else
        combined

    preSlash.replaceAll("//", "/")
  }

  protected[controller] val strPattern = "^" + path.replaceAll("""\{.*?\}""", """(.*?)""") + "$"
  protected[controller] val pattern = strPattern.r

  def isMatch(contextPath: String) = {
    val actionTokens: Array[String] = path.split("/")
    val urlTokens: Array[String] = contextPath.replaceAll("//", "/").split("/")

    if (actionTokens.size == urlTokens.size) {
      var map = Map[String, String]()

      for (x <- 0 to urlTokens.size - 1)
        map += urlTokens(x) -> actionTokens(x)

      map.forall((nvp) => nvp._1 == nvp._2 || nvp._2.startsWith("{"))
    }
    else
      false
  }

  def extractParameterValues(contextPath: String) = {
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

  def compare(that: Path) = this.path.compareTo(that.path)
}