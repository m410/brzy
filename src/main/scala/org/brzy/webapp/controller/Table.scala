package org.brzy.webapp.controller

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
case class Table[T](list: List[T], pageStart: Int, pageLimit: Int, total: Long) {

  def hasPrevious: Boolean = pageStart - pageLimit >= 0

  def hasNext: Boolean = pageStart + pageLimit < total

  def pagesBefore: Array[Int] = {
    val x = for (i <- 1 to pageStart; if i % pageLimit == 0) yield i
    x.toArray
  }

  def currentPage: Int = pageStart

  def pagesAfter: Array[Long] = {
    val x = for (i <- pageStart+pageLimit to total; if i % pageLimit == 0) yield i
    x.toArray
  }
}
