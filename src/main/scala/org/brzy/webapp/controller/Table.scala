package org.brzy.webapp.controller

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
case class Table[T](
        list: List[T],
        pageStart: Long,
        pageLimit: Long,
        total: Long,
        sort: String = "name",
        order: String = "desc"
        ) {

  def hasPrevious: Boolean = pageStart - pageLimit >= 0

  def hasNext: Boolean = pageStart + pageLimit < total

  def pagesBefore: Array[Long] = {
    val x = for (i <- 1L to pageStart; if i % pageLimit == 0) yield i
    x.toArray
  }

  def currentPage: Long = pageStart

  def pagesAfter: Array[Long] = {
    val x = for (i <- pageStart+pageLimit to total; if i % pageLimit == 0) yield i
    x.toArray
  }
}
