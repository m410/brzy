package org.brzy.squeryl

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Ignore
import org.junit.Assert._
import org.brzy.mock.Person
import org.brzy.mvc.validator.Validation
import javax.validation.{Validation => jValidation}

class ValidationTest extends JUnitSuite {
  @Test @Ignore def testValidation = {
    val person = new Person()
		person.getClass.getMethods.foreach(m=>{
			println("  -" + m)
			m.getAnnotations.foreach(x=>println("    m=" + x))
		})
		
		person.getClass.getConstructors.foreach(c=>{
			println("  @" + c)
			c.getAnnotations.foreach(x=>println("    a=" + x))
			c.getParameterAnnotations.foreach(x=> x.foreach(y=>{
				print("    [")
				print(y)
				print(", ")
				println("]")
			}))
		})
    val validationFactory = jValidation.buildDefaultValidatorFactory
    val valid = Validation[Person](validationFactory.getValidator.validate(person))
    assertNotNull(valid)
    assertFalse(valid.passes)
    assertEquals(2,valid.violations.size)
  }
}