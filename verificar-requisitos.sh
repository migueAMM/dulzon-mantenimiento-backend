#!/bin/bash
# =============================================================
#  VERIFICADOR DE REQUISITOS - Dulzón Mantenimiento Backend
#  Ejecutar con: bash verificar-requisitos.sh
# =============================================================

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # Sin color

OK="✅"
FAIL="❌"
WARN="⚠️ "

echo ""
echo -e "${BLUE}=============================================="
echo -e "  VERIFICADOR DE REQUISITOS - DULZÓN BACKEND"
echo -e "==============================================${NC}"
echo ""

ERRORS=0

# ── 1. JAVA ──────────────────────────────────────────────────
echo -e "${BLUE}[1/4] Verificando Java...${NC}"

if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -1 | grep -oP '(?<=version ")[^"]+' | cut -d'.' -f1)
    # Java 9+ reporta solo el major version
    if [ -z "$JAVA_VERSION" ]; then
        JAVA_VERSION=$(java -version 2>&1 | head -1 | awk -F'"' '{print $2}' | cut -d'.' -f1)
    fi

    if [ "$JAVA_VERSION" -ge 21 ] 2>/dev/null; then
        echo -e "  ${OK} Java $JAVA_VERSION encontrado en: $(which java)"
    elif [ "$JAVA_VERSION" -ge 17 ] 2>/dev/null; then
        echo -e "  ${WARN} Java $JAVA_VERSION encontrado - funciona pero recomendamos Java 21"
    else
        echo -e "  ${FAIL} Java $JAVA_VERSION es muy antiguo. Necesitas Java 21+"
        echo -e "     Descarga: https://adoptium.net/es/temurin/releases/?version=21"
        ERRORS=$((ERRORS+1))
    fi
else
    echo -e "  ${FAIL} Java NO encontrado"
    echo -e "     Descarga Java 21 LTS: https://adoptium.net/es/temurin/releases/?version=21"
    ERRORS=$((ERRORS+1))
fi

# ── 2. MAVEN ─────────────────────────────────────────────────
echo ""
echo -e "${BLUE}[2/4] Verificando Maven...${NC}"

if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn --version 2>&1 | head -1 | awk '{print $3}')
    echo -e "  ${OK} Maven $MVN_VERSION encontrado en: $(which mvn)"
else
    # Verificar si existe mvnw en el directorio actual
    if [ -f "./mvnw" ]; then
        echo -e "  ${OK} Maven Wrapper (./mvnw) encontrado en el proyecto - NO necesitas instalar Maven"
    else
        echo -e "  ${WARN} Maven no encontrado globalmente"
        echo -e "     Opciones:"
        echo -e "     a) Instalar Maven: https://maven.apache.org/download.cgi"
        echo -e "     b) Usar el mvnw incluido en el proyecto (./mvnw en Linux/Mac)"
        echo -e "     c) En Windows usar mvnw.cmd"
    fi
fi

# ── 3. MARIADB ───────────────────────────────────────────────
echo ""
echo -e "${BLUE}[3/4] Verificando MariaDB...${NC}"

MARIADB_OK=false

# Intentar conectar a MariaDB
if command -v mariadb &> /dev/null; then
    echo -e "  ${OK} Cliente MariaDB encontrado: $(mariadb --version 2>&1 | head -1)"
    MARIADB_OK=true
elif command -v mysql &> /dev/null; then
    # mysql client puede conectar a MariaDB también
    MYSQL_VER=$(mysql --version 2>&1 | head -1)
    echo -e "  ${OK} Cliente MySQL/MariaDB encontrado: $MYSQL_VER"
    MARIADB_OK=true
else
    echo -e "  ${WARN} No se encontró cliente de MariaDB/MySQL en PATH"
fi

# Verificar si el servicio está corriendo
if systemctl is-active --quiet mariadb 2>/dev/null; then
    echo -e "  ${OK} Servicio MariaDB está ACTIVO"
elif systemctl is-active --quiet mysql 2>/dev/null; then
    echo -e "  ${OK} Servicio MySQL/MariaDB está ACTIVO"
elif pgrep -x "mysqld" > /dev/null 2>&1; then
    echo -e "  ${OK} Proceso mysqld está corriendo"
else
    echo -e "  ${FAIL} Servicio MariaDB NO está corriendo"
    echo -e "     Linux:  sudo systemctl start mariadb"
    echo -e "     Mac:    brew services start mariadb"
    echo -e "     Instalar: https://mariadb.org/download/"
    ERRORS=$((ERRORS+1))
fi

# Verificar conexión real con credenciales del proyecto
echo -e "  Probando conexión con root/1234..."
if mysql -u root -p1234 -e "SELECT 1;" > /dev/null 2>&1 || \
   mariadb -u root -p1234 -e "SELECT 1;" > /dev/null 2>&1; then
    echo -e "  ${OK} Conexión a MariaDB exitosa (root/1234)"

    # Verificar si existe la base de datos
    DB_EXISTS=$(mysql -u root -p1234 -e "SHOW DATABASES LIKE 'dulzon_mantenimiento';" 2>/dev/null | grep -c "dulzon_mantenimiento" || echo "0")
    if [ "$DB_EXISTS" -gt 0 ]; then
        echo -e "  ${OK} Base de datos 'dulzon_mantenimiento' existe"
    else
        echo -e "  ${WARN} Base de datos 'dulzon_mantenimiento' NO existe aún"
        echo -e "     Se creará automáticamente al arrancar el proyecto"
    fi
else
    echo -e "  ${WARN} No se pudo conectar con root/1234"
    echo -e "     Si tu contraseña es diferente, edita: src/main/resources/application.properties"
    echo -e "     Línea: spring.datasource.password=TU_CONTRASEÑA"
fi

# ── 4. PUERTO 8080 ───────────────────────────────────────────
echo ""
echo -e "${BLUE}[4/4] Verificando puerto 8080...${NC}"

if lsof -i :8080 > /dev/null 2>&1 || netstat -tuln 2>/dev/null | grep -q ":8080 "; then
    echo -e "  ${WARN} Puerto 8080 está en uso - puede haber otro servidor corriendo"
    echo -e "     Detén el proceso o cambia el puerto en application.properties:"
    echo -e "     server.port=8081"
else
    echo -e "  ${OK} Puerto 8080 disponible"
fi

# ── RESUMEN ──────────────────────────────────────────────────
echo ""
echo -e "${BLUE}==============================================${NC}"
if [ $ERRORS -eq 0 ]; then
    echo -e "${GREEN}  ✅ TODO OK - Puedes arrancar el proyecto con:${NC}"
    echo ""
    echo -e "     ./mvnw spring-boot:run"
    echo -e "         o"
    echo -e "     mvn spring-boot:run"
    echo ""
    echo -e "     Luego abre: http://localhost:8080/api/mantenimiento/hoy"
else
    echo -e "${RED}  ❌ HAY $ERRORS PROBLEMA(S) - Resuélvelos antes de continuar${NC}"
fi
echo -e "${BLUE}==============================================${NC}"
echo ""
