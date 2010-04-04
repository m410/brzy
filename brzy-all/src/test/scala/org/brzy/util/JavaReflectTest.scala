package org.brzy.util

import org.junit.Assert._
import org.brzy.mock.UserController
import org.brzy.mock.Person
import org.junit.{Ignore, Test}
import scala.collection.mutable.WrappedArray._

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class JavaReflectTest {

  @Test
  @Ignore
  def testClassAnnotations = {
    val mock = new UserController
    val annotations = JavaReflect.annotations(mock)
    assertNotNull(annotations)
    assertEquals(1,annotations.size)
  }

  @Test
  @Ignore
  def testMethodAnnotations = {
    val mock = new UserController
    val clazz = mock.getClass

    val declareMethods = clazz.getDeclaredMethods

    for(x <- declareMethods) {
      println("x="+x)
      println("x.annotations="+x.getDeclaredAnnotations)
      println("x.parameters="+x.getParameterTypes)
      println("x.return="+x.getReturnType)
    }

    var methods = declareMethods.toList
    println("methods=" + methods)

    val annotations = JavaReflect.annotations(methods(0))
    assertNotNull(annotations)
    assertEquals(1,annotations.size)
  }

  @Test
  @Ignore
  def testMethods = {
    val mock = new UserController
    val methods:Array[_] = JavaReflect.methods(mock)
    methods.foreach(x => println("method=" + x))
    assertNotNull(methods)
    assertEquals(1,methods.size)
  }

  @Test
  @Ignore
  def testMethodReturn = {
    val mock = new UserController
    val method = mock.list _
    val returnType = JavaReflect.methodReturn(method)
    println("returnType=" + returnType)
    assertNotNull(returnType)
  }

  @Test
  @Ignore
  def testMethodArguments = {
    val mock = new UserController
    val method = mock.list _
    val arguments = JavaReflect.methodArguments(method)
    arguments.foreach(x => println("method_args=" + x))
    assertNotNull(arguments)
    assertEquals(1,arguments.size)
  }

	@Test
	def testConstructors = {
		val clazz = classOf[Person]
		clazz.getConstructors.foreach(println _)
		assertTrue(clazz.getConstructors.length == 2)
	}
}
