@echo off
set ERROR_CODE=0

if not exist "apache-maven-3.9.6-bin.zip" (
    echo Downloading Maven...
    powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip' -OutFile 'apache-maven-3.9.6-bin.zip'"
    if %ERRORLEVEL% NEQ 0 (
        echo Download failed. Please check your internet connection.
        exit /b %ERRORLEVEL%
    )
)

if not exist "apache-maven-3.9.6" (
    echo Extracting Maven...
    powershell -Command "Expand-Archive -Path 'apache-maven-3.9.6-bin.zip' -DestinationPath . -Force"
    if %ERRORLEVEL% NEQ 0 (
        echo Extraction failed.
        exit /b %ERRORLEVEL%
    )
)

echo Running Application...
call "apache-maven-3.9.6\bin\mvn.cmd" spring-boot:run
if %ERRORLEVEL% NEQ 0 (
    echo Build failed.
    exit /b %ERRORLEVEL%
)
