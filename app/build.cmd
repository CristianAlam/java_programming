@echo off
rem Builds the app for one unit:  build.cmd 2   ->  Unit2.jar
rem The Java code (src) and common files are shared; the content comes from units\unitN.
rem Needs a JDK 22 or newer: set JAVA_HOME to it first.
setlocal
if "%~1"=="" (
    echo Usage: build.cmd UNIT_NUMBER    for example: build.cmd 2
    exit /b 1
)
if "%JAVA_HOME%"=="" (
    echo Please set JAVA_HOME to a JDK 22 or newer.
    exit /b 1
)
set "HERE=%~dp0"
set "UNIT=%HERE%units\unit%~1"
if not exist "%UNIT%\unit.properties" (
    echo No content for unit %~1: %UNIT%\unit.properties is missing.
    exit /b 1
)
set "OUT=%HERE%build\unit%~1"
if exist "%OUT%" rmdir /s /q "%OUT%"
mkdir "%OUT%\classes"

pushd "%HERE%src"
"%JAVA_HOME%\bin\javac" --release 22 -encoding UTF-8 -d "%OUT%\classes" app\*.java
if errorlevel 1 (popd & exit /b 1)
popd
xcopy /e /i /q /y "%HERE%common" "%OUT%\classes" >nul
xcopy /e /i /q /y "%UNIT%" "%OUT%\classes" >nul
"%JAVA_HOME%\bin\jar" --create --file "%HERE%Unit%~1.jar" --manifest "%HERE%manifest.txt" -C "%OUT%\classes" . || exit /b 1

echo Built %HERE%Unit%~1.jar
