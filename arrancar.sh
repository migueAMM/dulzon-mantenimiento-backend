#!/bin/bash
# =============================================================
#  ARRANCAR PROYECTO - Dulzón Mantenimiento Backend
#  Uso: bash arrancar.sh [test|run|build]
# =============================================================

MODO=${1:-run}  # Por defecto: run

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; BLUE='\033[0;34m'; NC='\033[0m'

# Decidir si usar mvnw o mvn
if [ -f "./mvnw" ]; then
    MVN="./mvnw"
elif command -v mvn &> /dev/null; then
    MVN="mvn"
else
    echo -e "${RED}Error: ni mvnw ni mvn encontrados${NC}"
    exit 1
fi

chmod +x "$MVN" 2>/dev/null

case "$MODO" in
  test)
    echo -e "${BLUE}▶ Ejecutando tests (usa H2 en memoria, no necesita MariaDB)...${NC}"
    $MVN test
    ;;
  build)
    echo -e "${BLUE}▶ Compilando JAR...${NC}"
    $MVN clean package -DskipTests
    echo -e "${GREEN}▶ JAR generado en: target/mantenimiento-0.0.1-SNAPSHOT.jar${NC}"
    echo -e "▶ Ejecutar con: java -jar target/mantenimiento-0.0.1-SNAPSHOT.jar"
    ;;
  run|*)
    echo -e "${BLUE}▶ Arrancando servidor en http://localhost:8080 ...${NC}"
    echo -e "${YELLOW}   Presiona Ctrl+C para detener${NC}"
    echo ""
    $MVN spring-boot:run
    ;;
esac
