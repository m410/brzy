@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Brzy Fab(ricate) Start Up Batch script
@REM ----------------------------------------------------------------------------

@echo off

set LAUNCHER=org.brzy.fab.BuildMain
set JAVA_OPTS="-Xmx512m"

@REM set %HOME% to equivalent of $HOME
if "%HOME%" == "" (set "HOME=%HOMEDRIVE%%HOMEPATH%")

set ERROR_CODE=0

@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" @setlocal
if "%OS%"=="WINNT" @setlocal

@REM ==== START VALIDATION ====
if not "%JAVA_HOME%" == "" goto OkJHome

echo.
echo ERROR: JAVA_HOME not found in your environment.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation
echo.
goto error

:OkJHome
if exist "%JAVA_HOME%\bin\java.exe" goto chkMHome

echo.
echo ERROR: JAVA_HOME is set to an invalid directory.
echo JAVA_HOME = "%JAVA_HOME%"
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation
echo.
goto error

:chkMHome
if not "%BRZY_HOME%"=="" goto valMHome

if "%OS%"=="Windows_NT" SET "BRZY_HOME=%~dp0.."
if "%OS%"=="WINNT" SET "BRZY_HOME=%~dp0.."
if not "%BRZY_HOME%"=="" goto valMHome

echo.
echo ERROR: BRZY_HOME not found in your environment.
echo Please set the BRZY_HOME variable in your environment to match the
echo location of the Maven installation
echo.
goto error

:valMHome

:stripMHome
if not "_%BRZY_HOME:~-1%"=="_\" goto checkMBat
set "BRZY_HOME=%BRZY_HOME:~0,-1%"
goto stripMHome

:checkMBat
if exist "%BRZY_HOME%\bin\fab.bat" goto init

echo.
echo ERROR: BRZY_HOME is set to an invalid directory.
echo BRZY_HOME = "%BRZY_HOME%"
echo Please set the BRZY_HOME variable in your environment to match the
echo location of the Brzy installation
echo.
goto error
@REM ==== END VALIDATION ====

:init
@REM Decide how to startup depending on the version of windows

@REM -- Windows NT with Novell Login
if "%OS%"=="WINNT" goto WinNTNovell

@REM -- Win98ME
if NOT "%OS%"=="Windows_NT" goto Win9xArg

:WinNTNovell

@REM -- 4NT shell
if "%@eval[2+2]" == "4" goto 4NTArgs

@REM -- Regular WinNT shell
set BRZY_CMD_LINE_ARGS=%*
goto endInit

@REM The 4NT Shell from jp software
:4NTArgs
set BRZY_CMD_LINE_ARGS=%$
goto endInit

:Win9xArg
@REM Slurp the command line arguments.  This loop allows for an unlimited number
@REM of agruments (up to the command line limit, anyway).
set BRZY_CMD_LINE_ARGS=
:Win9xApp
if %1a==a goto endInit
set BRZY_CMD_LINE_ARGS=%BRZY_CMD_LINE_ARGS% %1
shift
goto Win9xApp

@REM Reaching here means variables are defined and arguments have been captured
:endInit
SET BRZY_JAVA_EXE="%JAVA_HOME%\bin\java.exe"

@REM -- 4NT shell
if "%@eval[2+2]" == "4" goto 4NTCWJars

@REM -- Regular WinNT shell
set JARS=
for %%i in ("%BRZY_HOME%"\lib\*) do call :append %%i
goto runbrzy

:append
set JARS=%JARS%;%1
goto :eof

@REM The 4NT Shell from jp software
:4NTCWJars
for %%i in ("%BRZY_HOME%\lib\*") do set JARS="%%i"
goto runbrzy

@REM Start BRZY
:runbrzy

%BRZY_JAVA_EXE% %JAVA_OPTS% -classpath %JARS%  "-Dbrzy.home=%BRZY_HOME%" %LAUNCHER% %BRZY_CMD_LINE_ARGS%
if ERRORLEVEL 1 goto error
goto end

:error
if "%OS%"=="Windows_NT" @endlocal
if "%OS%"=="WINNT" @endlocal
set ERROR_CODE=1

:end
@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" goto endNT
if "%OS%"=="WINNT" goto endNT

@REM For old DOS remove the set variables from ENV - we assume they were not set
@REM before we started - at least we don't leave any baggage around
set BRZY_JAVA_EXE=
set BRZY_CMD_LINE_ARGS=
goto postExec

:endNT
@endlocal & set ERROR_CODE=%ERROR_CODE%

:postExec


cmd /C exit /B %ERROR_CODE%


