package org.brzy.jpa

import javax.persistence.Query

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
class RichQuery(query : Query) {
  def getTypedList[T] = query.getResultList.asInstanceOf[List[T]]
}

object RichQuery {
  implicit def richQuery(q : Query) = new RichQuery(q)
}