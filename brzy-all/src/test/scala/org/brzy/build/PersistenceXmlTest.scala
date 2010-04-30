package org.brzy.build

import org.junit.Test
import org.junit.Assert._
import java.util.HashMap
import org.brzy.config.{PluginConfig, AppConfig}

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class PersistenceXmlTest {

  @Test
  def testPersistence = {
    val config = new AppConfig()
    config.application = new org.brzy.config.Application
    config.application.group_id = "org.brzy.mock"
    config.persistence = Array(new PluginConfig)
    config.persistence(0).name = "brzy-app"
    config.persistence(0).implementation = "scala-jpa"
    config.persistence(0).version = "latest"
    config.persistence(0).properties = new java.util.HashMap[String,String]
    config.persistence(0).properties.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect")
    config.persistence(0).properties.put("hibernate.connection.url", "jdbc:hsqldb:brzy-database")
    config.persistence(0).properties.put("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver")
    config.persistence(0).properties.put("hibernate.connection.username", "sa")
    config.persistence(0).properties.put("hibernate.connection.password", "")
    config.persistence(0).properties.put("hibernate.hbm2ddl.auto", "update")
    val persistence = new PersistenceXml(config)
    assertNotNull(persistence)
    println(persistence.body)
    assertNotNull(persistence.body)
  }
}