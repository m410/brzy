package org.brzy.ejb

import mock.MyBeanServiceImpl
import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import org.springframework.mock.jndi.SimpleNamingContextBuilder

class EjbServiceTest extends JUnitSuite {
  val builder = new SimpleNamingContextBuilder
  builder.bind("java:/comp/env/MyBeanService", new MyBeanServiceImpl)
  builder.activate

  @Test def testLookup = {
    val config = new EjbModConfig(Map[String,AnyRef](
      "name" -> "EJB",
      "beans" -> List(
          Map(
            "service_name" -> "myBeanService",
            "remote_interface" -> "org.brzy.ejb.mock.MyBeanService",
            "jndi_name" -> "java:/comp/env/MyBeanService")
        )
      ))
    val provider = new EjbModProvider(config)
    assertNotNull(provider.serviceMap)
    assertEquals(1, provider.serviceMap.size)
  }
}

