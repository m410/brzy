#!/bin/sh
CLASSPATH=$BRZY_HOME/lib/annotations-api.jar:\
$BRZY_HOME/lib/catalina-ant.jar:\
$BRZY_HOME/lib/catalina-ha.jar:\
$BRZY_HOME/lib/catalina-tribes.jar:\
$BRZY_HOME/lib/catalina.jar:\
$BRZY_HOME/lib/el-api.jar:\
$BRZY_HOME/lib/jasper-el.jar:\
$BRZY_HOME/lib/jasper-jdt.jar:\
$BRZY_HOME/lib/jasper.jar:\
$BRZY_HOME/lib/jsp-api.jar:\
$BRZY_HOME/lib/servlet-api.jar:\
$BRZY_HOME/lib/tomcat-coyote.jar:\
$BRZY_HOME/lib/tomcat-dbcp.jar:\
$BRZY_HOME/lib/tomcat-i18n-es.jar:\
$BRZY_HOME/lib/tomcat-i18n-fr.jar:\
$BRZY_HOME/lib/tomcat-i18n-ja.jar:\
$BRZY_HOME/lib/brzy-all-0.1.jar

echo $CLASSPATH

java -classpath $CLASSPATH org.brzy.shell.RunWebApplicationTomcat
