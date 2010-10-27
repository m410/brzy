package org.brzy.validator.constraints

import javax.validation.ConstraintValidatorContext
import org.brzy.validator.MessageInterpolator
import java.util.Locale

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
case class Pattern(
        pattern:String,
        message:String = "{org.brzy.validator.constraints.Pattern.message}"
    ) extends ConstraintValidator {

  def isValid(field: AnyRef) =
      pattern.r.findFirstMatchIn(field.asInstanceOf[String]) match {
        case Some(str) => true
        case _ => false
      }

  private[this] val map = Map("pattern"->pattern)
  override def interpolate(locale: Locale) =  MessageInterpolator.interpolate(message,locale,map)
}