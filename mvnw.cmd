@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script (Windows)
@REM ----------------------------------------------------------------------------
@echo off
setlocal enabledelayedexpansion

set MAVEN_PROJECTBASEDIR=%~dp0
set MAVEN_WRAPPER_PROPERTIES=%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.properties

@REM Read distributionUrl from properties
for /f "tokens=1,2 delims==" %%a in (%MAVEN_WRAPPER_PROPERTIES%) do (
    if "%%a"=="distributionUrl" set DISTRIBUTION_URL=%%b
)

@REM Set local Maven home in D drive to save C space
set MAVEN_USER_HOME=D:\maven-wrapper
set MVNW_REPOURL=https://repo.maven.apache.org/maven2

@REM Extract version from URL
for %%f in ("%DISTRIBUTION_URL%") do set MAVEN_ZIP_NAME=%%~nf
set MAVEN_HOME=%MAVEN_USER_HOME%\%MAVEN_ZIP_NAME%

if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo Descargando Apache Maven...
    if not exist "%MAVEN_USER_HOME%" mkdir "%MAVEN_USER_HOME%"

    set MAVEN_ZIP=%MAVEN_USER_HOME%\maven.zip
    powershell -Command "Invoke-WebRequest -Uri '%DISTRIBUTION_URL%' -OutFile '!MAVEN_ZIP!'"
    powershell -Command "Expand-Archive -Path '!MAVEN_ZIP!' -DestinationPath '%MAVEN_USER_HOME%' -Force"
    del "!MAVEN_ZIP!"
    echo Maven descargado en %MAVEN_HOME%
)

@REM Use local repo in D drive (avoid filling C)
set MAVEN_OPTS=-Dmaven.repo.local=D:\maven-repo %MAVEN_OPTS%

"%MAVEN_HOME%\bin\mvn.cmd" %*
