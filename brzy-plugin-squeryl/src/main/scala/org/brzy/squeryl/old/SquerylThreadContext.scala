package org.brzy.squeryl.old

import org.brzy.persistence.ThreadContext
import org.squeryl.Session

/**
 * @author Michael Fortin
 * @version $Id: $
 */
case class SquerylThreadContext(session:Session) extends ThreadContext {
  def start = session.bindToCurrentThread
  def close =  session.unbindFromCurrentThread
}