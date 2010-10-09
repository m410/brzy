set SCRIPT_DIR=%~dp0
@echo off
 setLocal EnableDelayedExpansion
 set CLASSPATH="
 for /R ./lib %%a in (*.jar) do (
   set CLASSPATH=!CLASSPATH!;%%a
 )
 set CLASSPATH=!CLASSPATH!"
 echo !CLASSPATH!

rem http://download.oracle.com/javase/6/docs/technotes/tools/windows/classpath.html
rem http://stackoverflow.com/questions/524081/bat-file-to-create-java-classpath

java -Xmx512M -cp %CLASSPATH% org.brzy.BuildMain %*
