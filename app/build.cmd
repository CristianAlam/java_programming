@echo off
rem Builds Unit1.jar. Needs a JDK 22 or newer: set JAVA_HOME to it first.
setlocal
if "%JAVA_HOME%"=="" (
    echo Please set JAVA_HOME to a JDK 22 or newer.
    exit /b 1
)
set "HERE=%~dp0"
set "OUT=%HERE%build"
if exist "%OUT%" rmdir /s /q "%OUT%"
mkdir "%OUT%\classes"

pushd "%HERE%src"
"%JAVA_HOME%\bin\javac" --release 22 -encoding UTF-8 -d "%OUT%\classes" app\*.java
if errorlevel 1 (popd & exit /b 1)
popd
xcopy /e /i /q /y "%HERE%resources" "%OUT%\classes" >nul
"%JAVA_HOME%\bin\jar" --create --file "%HERE%Unit1.jar" --manifest "%HERE%manifest.txt" -C "%OUT%\classes" . || exit /b 1

echo Built %HERE%Unit1.jar
