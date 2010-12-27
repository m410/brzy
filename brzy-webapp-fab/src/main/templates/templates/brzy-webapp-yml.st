#
# application configuration
# configuration Reference: http://brzy.org/docs/config_reference
# configuration dump: >brzy info
application:
  name: $name$
  org: $package$
  version: "$version$"
  author: UPDATE ME
  description: UPDATE ME
  application_class: $package$.Application

persistence:
  - name: brzy-squeryl
    org: org.brzy.mod
    version: "0.9"
    driver: org.h2.Driver
    user_name: sa
    password: ""
    url: jdbc:h2:test_db


repositories:
  - {id: scala-tools, url: http://nexus.scala-tools.org/content/repositories/releases}
  - {id: maven-central, url: http://repo1.maven.org/maven2}

dependencies:
  - {conf: compile, org: com.h2database, name: h2, rev: "1.2.133", transitive: false}
  - {conf: test, org: junit, name: junit, rev: "4.8.1"}
  - {conf: test, org: org.scalatest, name: scalatest, rev: "1.2", transitive: false}

modules:
  - name: brzy-tomcat
    org: org.brzy
    version: "0.5"

logging:
  provider: logback
  appenders:
    - name: STDOUT
      appender_class: ch.qos.logback.core.ConsoleAppender
      layout: ch.qos.logback.classic.encoder.PatternLayoutEncoder
      pattern: "%-4relative [%thread] %-5level %class - %msg%n"
  loggers:
    - {name: org.brzy, level: trace}
    - {name: $package$, level: trace}
  root:
    level: INFO
    ref: [STDOUT]
web_xml:
  - description: *desc
  - display-name: *name

# override defaults for any environment
environment_overrides:
  - environment: development
    dependencies:
      - {conf: compile, org: com.h2database, name: h2, rev: "1.2.133"}
  - environment: test
  - environment: production
    dependencies:
      - {conf: compile, org: postgresql, name: postgresql, rev: "8.4-701.jdbc4"}
    logging:
      appenders:
        - name: FILE
          appender_class: ch.qos.logback.core.rolling.RollingFileAppender
          file: /var/log/tomcat6/brzy.log
          rolling_policy: ch.qos.logback.core.rolling.TimeBasedRollingPolicy
          file_name_pattern: "logs/brzy.%d{yyyy-MM-dd}.log"
          layout: ch.qos.logback.classic.encoder.PatternLayoutEncoder
          pattern: "%-4relative [%thread] %-5level %class - %msg%n"
      root:
        ref: [FILE]
    persistence:
      - name: brzy-squeryl
        driver: org.postgresql.Driver
        user_name: demo
        password: demo
        url: jdbc:postgresql://localhost/squeryl_scalate
