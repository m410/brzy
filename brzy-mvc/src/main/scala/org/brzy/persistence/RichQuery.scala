package org.brzy.persistence

import javax.persistence.Query

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
class RichQuery(query : Query) {
  def getTypedList[T] = query.getResultList.asInstanceOf[List[T]]
}

object RichQuery {
  implicit def richQuery(q : Query) = new RichQuery(q)
}