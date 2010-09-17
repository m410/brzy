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
    org: org.brzy
    version: "0.8-SNAPSHOT"
    driver: org.h2.Driver
    user_name: sa
    password: ""
    url: jdbc:h2:test_db

repositories:

dependencies:

modules:
  - name: brzy-tomcat
    org: org.brzy
    version: "0.5-SNAPSHOT"
logging:
  provider: logback
  appenders:
    - name: STDOUT
      appender_class: ch.qos.logback.core.ConsoleAppender
      layout: ch.qos.logback.classic.encoder.PatternLayoutEncoder
      pattern: "%-4relative [%thread] %-5level %class - %msg%n"
    - name: FILE
      appender_class: ch.qos.logback.core.rolling.RollingFileAppender
      file: /var/log/tomcat6/brzy.log
      rolling_policy: ch.qos.logback.core.rolling.TimeBasedRollingPolicy
      file_name_pattern: "logs/brzy.%d{yyyy-MM-dd}.log"
      layout: ch.qos.logback.classic.encoder.PatternLayoutEncoder
      pattern: "%-4relative [%thread] %-5level %class - %msg%n"
  loggers:
    - {name: org.brzy, level: trace}
  root:
    level: INFO
    ref: [FILE]
web_xml:
  - description: $name$ brzy web application
  - display-name: $name$
# override defaults for any environment
environment_overrides:
  - environment: development
    logging:
      root:
        ref: [STDOUT]
  - environment: test
  - environment: production
    logging:
      root:
        ref: [FILE]
