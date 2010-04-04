package org.brzy.saved.intercept

import org.aspectj.weaver.tools.{PointcutExpression,PointcutParser}
import java.lang.annotation.Annotation

/**
 *
 * @author Michael Fortin
 * @version $Id: $
 */
trait Interceptor extends InterceptHandler {
  protected val parser = PointcutParser.getPointcutParserSupportingAllPrimitivesAndUsingContextClassloaderForResolution

  protected def matches(pointcut: PointcutExpression, invocation: Invocation): Boolean = {
    pointcut.matchesMethodExecution(invocation.method).alwaysMatches ||
    invocation.target.getClass.getDeclaredMethods.exists(pointcut.matchesMethodExecution(_).alwaysMatches) ||
    false
  }

  protected def matches(annotationClass: Class[_ <: Annotation], invocation: Invocation): Boolean = {
    invocation.method.isAnnotationPresent(annotationClass) ||
    invocation.target.getClass.isAnnotationPresent(annotationClass) ||
    false
  }
}