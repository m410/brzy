package org.brzy.webapp.action.response

import javax.servlet.{AsyncEvent, AsyncListener}

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
class BlankAsyncListener extends AsyncListener {
  def onComplete(p1: AsyncEvent) {}

  def onTimeout(p1: AsyncEvent) {}

  def onError(p1: AsyncEvent) {}

  def onStartAsync(p1: AsyncEvent) {}
}