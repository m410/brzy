package org.brzy.validator

import java.util.{ResourceBundle, Locale}
import java.lang.String
import collection.JavaConversions._

/**
 * Document Me..
 *
 * @author Michael Fortin
 * @version $Id : $
 */
object MessageInterpolator {
  private[this] val PatternExtract = """.*\{([\w\d\.-]+)\}.*""".r

  def interpolate(
        template: String,
        locale: Locale = Locale.getDefault,
        params: Map[String, AnyRef] = Map.empty) =
    replace(template, locale, params)

  protected[validator] def replace(template: String, locale: Locale, p:Map[String, AnyRef]): String =
    template match {
      case PatternExtract(inner) =>
        val bundle = ResourceBundle.getBundle("i18n/messages", locale)
        if (bundle.getKeys.contains(inner))
          replace(template.replace("{" + inner + "}", bundle.getString(inner)), locale, p)
        else if(p.contains(inner))
          replace(template.replace("{" + inner + "}", p(inner).toString), locale, p)
        else
          template
      case _ =>
        template
    }
}