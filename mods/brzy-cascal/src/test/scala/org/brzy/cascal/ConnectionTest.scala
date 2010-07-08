package org.brzy.cascal

import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import com.shorrockin.cascal.session._
import com.shorrockin.cascal.utils.Conversions._
import org.junit.{Ignore, Test}

/**
 * Document Me..
 *
 * @author Michael Fortin
 * @version $Id : $
 */

class ConnectionTest extends JUnitSuite {
  // needs to have the database running to work
  @Test @Ignore def testConnect = {
    val hosts = Host("localhost", 9160, 250) :: Nil
    val params = new PoolParams(10, ExhaustionPolicy.Fail, 500L, 6, 2)
    val pool = new SessionPool(hosts, params, Consistency.One)

    pool.borrow { session =>
      assertEquals(0,session.count("Keyspace1" \ "Standard2" \ "1"))
    }
  }
}