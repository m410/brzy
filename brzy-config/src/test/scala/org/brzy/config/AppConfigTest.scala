package org.brzy.config

import org.junit.Test
import org.junit.Assert._
import java.util.{HashMap => JHashMap, Map => JMap}
import org.ho.yaml.Yaml
import collection.JavaConversions._

/**
 * @author Michael Fortin
 * @version $Id : $
 */
class AppConfigTest {
  @Test
  def testLoad = {
    val url = getClass.getClassLoader.getResource("brzy-app.b.yml")
    val config = Yaml.load(url.openStream)
    config.asInstanceOf[JMap[String, AnyRef]].put("environment", "development")
    val app = new WebappConfig(config.asInstanceOf[JMap[String, AnyRef]].toMap)
    assertNotNull(app)
    assertNotNull(app.environment)
    assertEquals("development", app.environment)
    assertNotNull(app.application)
    assertNotNull(app.application.version)
    assertEquals("1.0.0", app.application.version)
    assertNotNull(app.application.name)
    assertEquals("Test app", app.application.name)
    assertNotNull(app.application.author)
    assertEquals("Fred", app.application.author)
    assertNotNull(app.application.description)
    assertEquals("Some Description", app.application.description)
    assertNotNull(app.application.groupId)
    assertEquals("org.brzy.sample", app.application.groupId)
    assertNotNull(app.application.artifactId)
    assertEquals("sample-jpa", app.application.artifactId)
    assertNotNull(app.application.applicationClass)
    assertEquals("org.brzy.mock.MockWebApp", app.application.applicationClass)
    assertNotNull(app.application.webappContext)
    assertEquals("mainapp", app.application.webappContext)
    assertNotNull(app.dependencies)
    assertEquals(2, app.dependencies.size)
    //  - {conf: compile, org: org.apache.wicket, name: wicket, rev: 1.4-rc6}
    //  - {conf: compile, org: org.fusesource.scalate, name: scalate-core, rev: "1.0"}
    assertNotNull(app.repositories)
    assertEquals(1, app.repositories.size)
    //  - id: fusesource
    //    url: http://someurl.com/test

    assertNotNull(app.plugins)
    assertEquals(1, app.plugins.size)
    //  - name: brzy-scalate
    //    config_class: org.brzy.plugin.ScalatePluginConfig
    //    group_id: org.brzy
    //    version: "0.1"
    //    dependencies:
    //      - lib: "test:test:test:1.0"
    //    web_xml:
    //      - anything: Test

    assertNotNull(app.persistence)
    assertEquals(1, app.persistence.size)
    //  - name: brzy-squeryl
    //    config_class: org.brzy.plugin.SquerylPluginConfig
    //    group_id: org.brzy
    //    version: "0.1"
    //    scan_package: *group
    //    properties:
    //      driver_class: org.postgresql.Driver
    //      user_name: test
    //      password:
    //      url: jdbc:postgresql://localhost/dev
    assertNotNull(app.logging)
    assertNotNull(app.logging.appenders)
    assertNotNull(app.logging.loggers)
    assertNotNull(app.logging.root)
    //  provider: logback
    //  appenders:
    //    - name: STDOUT
    //      appender_class: ch.qos.logback.core.ConsoleAppender
    //      layout: ch.qos.logback.classic.encoder.PatternLayoutEncoder
    //      pattern: "%-4relative [%thread] %-5level %class - %msg%n"
    //    - name: FILE
    //      appender_class: ch.qos.logback.core.rolling.RollingFileAppender
    //      file: logs/brzy.log
    //      rolling_policy: ch.qos.logback.core.rolling.TimeBasedRollingPolicy
    //      file_name_pattern: "brzy.%d{yyyy-MM-dd}.log"
    //      layout: ch.qos.logback.classic.encoder.PatternLayoutEncoder
    //      pattern: "%-4relative [%thread] %-5level %class - %msg%n"
    //  loggers:
    //    - name: org.brzy
    //      level: debug
    //  root:
    //    level: INFO
    //    ref: [FILE]
    assertNotNull(app.webXml)
    assertEquals(2, app.webXml.size)
    //  - servlet: {servlet-name: ScalateServlet, servlet-class: org.fusesource.scalate.servlet.TemplateEngineServlet, load-on-startup: 1}
    //  - servlet-mapping: { servlet-name: ScalateServlet, url-pattern: "*.ssp"}
  }

  //  @Test
  //  def testMerge3 = {
  //    val subConfig = new AppConfig
  //    subConfig.application = new org.brzy.config.Application
  //    subConfig.application.version = "1.1.2"
  //    subConfig.application.artifactId = "tester"
  //
  //    val appConfig = new AppConfig
  //    appConfig.application = new org.brzy.config.Application
  //		appConfig.environment = "app"
  //    appConfig.application.version = "1.1.1"
  //    appConfig.application.groupId = "org.group"
  //
  //    val defaultConfig = new AppConfig
  //    defaultConfig.application = new org.brzy.config.Application
  //    defaultConfig.application.name = "bob"
  //
  //    val mergedConfig = defaultConfig + appConfig + subConfig
  //    assertNotNull(mergedConfig)
  //    assertTrue(defaultConfig != mergedConfig)
  //    assertEquals("1.1.2", mergedConfig.application.version)
  //    assertEquals("bob", mergedConfig.application.name)
  //    assertEquals("tester", mergedConfig.application.artifactId)
  //    assertEquals("org.group", mergedConfig.application.groupId)
  //    assertNull( mergedConfig.application.webappContext)
  //  }
}