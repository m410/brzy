package org.brzy.persistence

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */

trait ThreadContext {

  def start
  def close
}