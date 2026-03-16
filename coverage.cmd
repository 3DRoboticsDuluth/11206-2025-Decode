@echo off
setlocal

set "REPORT=%~dp0TeamCode\build\reports\jacoco\jacocoTestReport\html\index.html"
set "MODE=%~1"

if "%MODE%"=="" set "MODE=refresh"

if /I "%MODE%"=="run" goto run
if /I "%MODE%"=="open" goto open
if /I "%MODE%"=="refresh" goto refresh

echo Usage: coverage.cmd [run^|open^|refresh]
exit /b 1

:run
call "%~dp0gradlew.bat" :TeamCode:jacocoTestReport
exit /b %ERRORLEVEL%

:open
if not exist "%REPORT%" (
    echo Coverage report not found:
    echo %REPORT%
    echo.
    echo Generate it with:
    echo   coverage.cmd run
    exit /b 1
)

start "" "%REPORT%"
exit /b 0

:refresh
call "%~dp0gradlew.bat" :TeamCode:jacocoTestReport
if errorlevel 1 exit /b %ERRORLEVEL%
goto open
