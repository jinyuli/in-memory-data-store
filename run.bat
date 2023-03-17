@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem
@rem  in-memory-data-store startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem This is normally unused
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

echo %APP_HOME%
echo %JAVA_HOME_RUN%

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and IN_MEMORY_DATA_STORE_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=--enable-preview -Xlog:gc*=info:file=gc.log::filecount=5,filesize=4m -Xms2048m -Xmx2048m -XX:G1HeapRegionSize=4m
@rem set DEFAULT_JVM_OPTS=-Xlog:gc+malloc+oopmap+safepoint+ref+region+reloc+logging=info:file=gc.log -XX:G1HeapRegionSize=4m

@rem Find java.exe
if defined JAVA_HOME_RUN goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute


echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME_RUN:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe


if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\build\libs\Jimds-1.0.0.jar

@rem %JAVA_EXE% -Xlog:help

@rem Execute in-memory-data-store
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %IN_MEMORY_DATA_STORE_OPTS%  -classpath "%CLASSPATH%" com.codeloam.memory.store.Main %*

:end
@rem End local scope for the variables with windows NT shell
if %ERRORLEVEL% equ 0 goto mainEnd

:fail
rem Set variable IN_MEMORY_DATA_STORE_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% equ 0 set EXIT_CODE=1
if not ""=="%IN_MEMORY_DATA_STORE_EXIT_CONSOLE%" exit %EXIT_CODE%
exit /b %EXIT_CODE%

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
