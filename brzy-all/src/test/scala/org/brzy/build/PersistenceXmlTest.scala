package org.brzy.build

import org.junit.Test
import org.junit.Assert._
import org.brzy.config.Config
import java.util.HashMap

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class PersistenceXmlTest {

  @Test
  def testPersistence = {
    val config = new Config()
    config.group_id = "org.brzy.mock"
    config.persistence_properties = new HashMap[String,String]
    config.persistence_properties.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect")
    config.persistence_properties.put("hibernate.connection.url", "jdbc:hsqldb:brzy-database")
    config.persistence_properties.put("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver")
    config.persistence_properties.put("hibernate.connection.username", "sa")
    config.persistence_properties.put("hibernate.connection.password", "")
    config.persistence_properties.put("hibernate.hbm2ddl.auto", "update")
    val persistence = new PersistenceXml(config)
    assertNotNull(persistence)
    println(persistence.body)
    assertNotNull(persistence.body)
  }
}