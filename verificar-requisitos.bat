@echo off
:: =============================================================
::  VERIFICADOR DE REQUISITOS - Dulzón Mantenimiento Backend
::  Ejecutar con: verificar-requisitos.bat  (doble clic o desde CMD)
:: =============================================================

echo.
echo ==============================================
echo   VERIFICADOR DE REQUISITOS - DULZON BACKEND
echo ==============================================
echo.

set ERRORS=0

:: ── 1. JAVA ─────────────────────────────────────────────────
echo [1/4] Verificando Java...

java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo   [X] Java NO encontrado
    echo       Descarga Java 21 LTS: https://adoptium.net/es/temurin/releases/?version=21
    set /a ERRORS+=1
) else (
    for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
        set JAVA_VER=%%g
    )
    echo   [OK] Java encontrado: %JAVA_VER%
    echo       Ruta: 
    where java
)
echo.

:: ── 2. MAVEN ────────────────────────────────────────────────
echo [2/4] Verificando Maven...

mvn --version >nul 2>&1
if %errorlevel% neq 0 (
    if exist "mvnw.cmd" (
        echo   [OK] Maven Wrapper encontrado (mvnw.cmd) - no necesitas instalar Maven
    ) else (
        echo   [!] Maven no encontrado
        echo       Usa el mvnw.cmd incluido en el proyecto: mvnw.cmd spring-boot:run
        echo       O descarga Maven: https://maven.apache.org/download.cgi
    )
) else (
    for /f "tokens=3" %%v in ('mvn --version 2^>^&1 ^| findstr /i "Apache Maven"') do (
        echo   [OK] Maven %%v encontrado
    )
)
echo.

:: ── 3. MARIADB ───────────────────────────────────────────────
echo [3/4] Verificando MariaDB...

:: Verificar cliente
mysql --version >nul 2>&1
if %errorlevel% equ 0 (
    echo   [OK] Cliente MySQL/MariaDB encontrado
) else (
    mariadb --version >nul 2>&1
    if %errorlevel% equ 0 (
        echo   [OK] Cliente MariaDB encontrado
    ) else (
        echo   [!] Cliente MariaDB no en PATH - verifica la instalacion
    )
)

:: Verificar servicio
sc query MySQL80 >nul 2>&1
if %errorlevel% equ 0 (
    sc query MySQL80 | findstr "RUNNING" >nul
    if %errorlevel% equ 0 (
        echo   [OK] Servicio MySQL80 ACTIVO
    ) else (
        echo   [X] Servicio MySQL80 esta DETENIDO
        echo       Inicia con: net start MySQL80
        echo       O abre Services (Win+R, services.msc) y busca MySQL80 o MariaDB
        set /a ERRORS+=1
    )
) else (
    sc query MariaDB >nul 2>&1
    if %errorlevel% equ 0 (
        sc query MariaDB | findstr "RUNNING" >nul
        if %errorlevel% equ 0 (
            echo   [OK] Servicio MariaDB ACTIVO
        ) else (
            echo   [X] Servicio MariaDB esta DETENIDO - Inicia con: net start MariaDB
            set /a ERRORS+=1
        )
    ) else (
        echo   [!] No se encontro servicio MySQL80 ni MariaDB
        echo       Descarga MariaDB: https://mariadb.org/download/
        echo       O instala XAMPP:  https://www.apachefriends.org/es/index.html
        set /a ERRORS+=1
    )
)

:: Verificar conexion
echo   Probando conexion con root/1234...
mysql -u root -p1234 -e "SELECT 'Conexion OK';" >nul 2>&1
if %errorlevel% equ 0 (
    echo   [OK] Conexion a MariaDB exitosa (root/1234)
    mysql -u root -p1234 -e "SHOW DATABASES LIKE 'dulzon_mantenimiento';" 2>nul | findstr "dulzon" >nul
    if %errorlevel% equ 0 (
        echo   [OK] Base de datos 'dulzon_mantenimiento' existe
    ) else (
        echo   [!] Base de datos 'dulzon_mantenimiento' no existe aun
        echo       Se creara automaticamente al arrancar el proyecto
    )
) else (
    echo   [!] No se pudo conectar con root/1234
    echo       Si tu contrasena es diferente, edita:
    echo       src\main\resources\application.properties
    echo       Linea: spring.datasource.password=TU_CONTRASENA
)
echo.

:: ── 4. PUERTO 8080 ──────────────────────────────────────────
echo [4/4] Verificando puerto 8080...
netstat -aon | findstr ":8080 " >nul 2>&1
if %errorlevel% equ 0 (
    echo   [!] Puerto 8080 en uso - puede haber otro proceso corriendo
    echo       Cambia el puerto en application.properties: server.port=8081
) else (
    echo   [OK] Puerto 8080 disponible
)
echo.

:: ── RESUMEN ──────────────────────────────────────────────────
echo ==============================================
if %ERRORS% equ 0 (
    echo   TODO OK - Arranca el proyecto con:
    echo.
    echo   mvnw.cmd spring-boot:run
    echo.
    echo   Luego abre: http://localhost:8080/api/mantenimiento/hoy
) else (
    echo   HAY %ERRORS% PROBLEMA(S) - Resolverlos antes de continuar
)
echo ==============================================
echo.
pause
