package org.brzy.validator

import javax.validation.ConstraintValidatorContext
import java.lang.String

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
class DefaultConstraintValidatorContext extends ConstraintValidatorContext {
  def buildConstraintViolationWithTemplate(p1: String) = null

  def getDefaultConstraintMessageTemplate = ""

  def disableDefaultConstraintViolation = {}
}