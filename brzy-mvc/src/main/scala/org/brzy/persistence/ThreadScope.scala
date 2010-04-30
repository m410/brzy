package org.brzy.persistence

/**
 * @author Michael Fortin
 * @version $Id: $
 */
object ThreadScope {

  private val threadLocal = new ThreadLocal[ThreadContext]

  def get: ThreadContext = threadLocal.get

  def doWith[R](ctx: ThreadContext)(function : => R) : R = {
    val original = get
    try {
      threadLocal.set(ctx)
      val functionReturn = function
      ctx.close
      functionReturn
    }
    finally {
      threadLocal.set(original)
    }
  }
}