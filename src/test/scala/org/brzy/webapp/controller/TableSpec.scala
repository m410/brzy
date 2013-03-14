package org.brzy.webapp.controller

import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers


class TableSpec extends WordSpec with ShouldMatchers {
  val list = List(
    "One0",
    "One1",
    "One2",
    "One3",
    "One4",
    "One5",
    "One6",
    "One7",
    "One8",
    "One9")

  "Table" should {
    "page in middle" in {
      val table = Table(list,10,10,49)
      table.total should equal (49)
      table.currentPage should equal (10)
      table.hasPrevious should equal (true)
      table.hasNext should equal (true)
      table.pagesAfter.size should equal (3)
      table.pagesBefore.size should equal (1)
    }
    "page at start" in {
      val table = Table(list,1,10,49)
      table.total should equal (49)
      table.currentPage should equal (1)
      table.hasPrevious should equal (false)
      table.hasNext should equal (true)
      table.pagesAfter.size should equal (3)
      table.pagesBefore.size should equal (0)
    }
    "page at end" in {
      val table = Table(list,40,10,49)
      table.total should equal (49)
      table.currentPage should equal (40)
      table.hasPrevious should equal (true)
      table.hasNext should equal (false)
      table.pagesAfter.size should equal (0)
      table.pagesBefore.size should equal (4)
    }
  }
}
