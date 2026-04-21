@echo off
:: =============================================================
::  ARRANCAR PROYECTO - Dulzón Mantenimiento Backend
::  Doble clic o desde CMD: arrancar.bat [test|run|build]
:: =============================================================

set MODO=%1
if "%MODO%"=="" set MODO=run

if "%MODO%"=="test" goto :test
if "%MODO%"=="build" goto :build
goto :run

:run
echo.
echo  Arrancando servidor en http://localhost:8080 ...
echo  Presiona Ctrl+C para detener
echo.
mvnw.cmd spring-boot:run
goto :end

:test
echo.
echo  Ejecutando tests (usa H2, no necesita MariaDB)...
echo.
mvnw.cmd test
goto :end

:build
echo.
echo  Compilando JAR...
echo.
mvnw.cmd clean package -DskipTests
echo.
echo  JAR generado: target\mantenimiento-0.0.1-SNAPSHOT.jar
echo  Ejecutar: java -jar target\mantenimiento-0.0.1-SNAPSHOT.jar
goto :end

:end
pause
