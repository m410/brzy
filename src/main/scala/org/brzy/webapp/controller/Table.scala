package org.brzy.webapp.controller

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
case class Table[T](list: List[T], pageStart: Int, pageSize: Int, total: Int) {

  def hasPrevious: Boolean = pageStart - pageSize >= 0

  def hasNext: Boolean = pageStart + pageSize < total

  def pagesBefore: Array[Int] = {
    val x = for (i <- 1 to pageStart; if i % pageSize == 0) yield i
    x.toArray
  }

  def currentPage: Int = pageStart

  def pagesAfter: Array[Int] = {
    val x = for (i <- pageStart+pageSize to total; if i % pageSize == 0) yield i
    x.toArray
  }
}
