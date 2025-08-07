#!/bin/bash

# Script de desarrollo mejorado para javaTower
# Facilita las tareas de desarrollo comunes

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
WHITE='\033[1;37m'
NC='\033[0m' # No Color

# Función para imprimir headers
print_header() {
    echo -e "${CYAN}╭─────────────────────────────────────────╮${NC}"
    echo -e "${CYAN}│${WHITE}        javaTower Development Tool       ${CYAN}│${NC}"
    echo -e "${CYAN}│${WHITE}           by joseAlejandro              ${CYAN}│${NC}"
    echo -e "${CYAN}╰─────────────────────────────────────────╯${NC}"
    echo
}

# Función para mostrar ayuda
show_help() {
    echo -e "${WHITE}Uso: $0 [comando]${NC}"
    echo
    echo -e "${YELLOW}Comandos disponibles:${NC}"
    echo -e "${GREEN}  help${NC}           Mostrar esta ayuda"
    echo -e "${GREEN}  clean${NC}          Limpiar proyecto"
    echo -e "${GREEN}  compile${NC}        Compilar proyecto"
    echo -e "${GREEN}  test${NC}           Ejecutar todas las pruebas"
    echo -e "${GREEN}  test-fast${NC}      Ejecutar solo pruebas rápidas"
    echo -e "${GREEN}  test-fix${NC}       Ejecutar solo tests que fallan"
    echo -e "${GREEN}  run${NC}            Ejecutar el juego"
    echo -e "${GREEN}  run-debug${NC}      Ejecutar con información de debug"
    echo -e "${GREEN}  package${NC}        Crear JAR ejecutable"
    echo -e "${GREEN}  coverage${NC}       Generar reporte de cobertura"
    echo -e "${GREEN}  performance${NC}    Ejecutar con monitor de performance"
    echo -e "${GREEN}  docs${NC}           Generar documentación JavaDoc"
    echo -e "${GREEN}  all${NC}            Compilar, probar y empaquetar"
    echo -e "${GREEN}  status${NC}         Mostrar estado del proyecto"
    echo -e "${GREEN}  fix-tests${NC}      Guía para arreglar tests fallidos"
    echo
    echo -e "${YELLOW}Ejemplos:${NC}"
    echo -e "  $0 compile          # Solo compilar"
    echo -e "  $0 test-fast        # Pruebas rápidas"
    echo -e "  $0 run-debug        # Ejecutar con debug"
    echo
}

# Función para verificar prerrequisitos
check_prerequisites() {
    echo -e "${BLUE}🔍 Verificando prerrequisitos...${NC}"
    
    if ! command -v java &> /dev/null; then
        echo -e "${RED}❌ Java no está instalado${NC}"
        exit 1
    fi
    
    if ! command -v mvn &> /dev/null; then
        echo -e "${RED}❌ Maven no está instalado${NC}"
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d. -f1)
    if [ "$JAVA_VERSION" -lt 17 ]; then
        echo -e "${YELLOW}⚠️  Se recomienda Java 17+. Versión actual: $(java -version 2>&1 | head -n 1)${NC}"
    else
        echo -e "${GREEN}✅ Java $(java -version 2>&1 | awk -F '"' '/version/ {print $2}')${NC}"
    fi
    
    echo -e "${GREEN}✅ Maven $(mvn -version 2>&1 | head -n 1 | awk '{print $3}')${NC}"
    echo
}

# Función para mostrar estado del proyecto
show_status() {
    echo -e "${BLUE}📊 Estado del Proyecto javaTower${NC}"
    echo -e "${BLUE}═══════════════════════════════════════${NC}"
    
    # Contar archivos de código
    JAVA_FILES=$(find src/main/java -name "*.java" | wc -l | xargs)
    TEST_FILES=$(find src/test/java -name "*.java" 2>/dev/null | wc -l | xargs)
    TOTAL_LINES=$(find src -name "*.java" -exec wc -l {} + 2>/dev/null | tail -n 1 | awk '{print $1}')
    
    echo -e "${WHITE}📁 Archivos de código:${NC} $JAVA_FILES archivos Java"
    echo -e "${WHITE}🧪 Archivos de prueba:${NC} $TEST_FILES archivos de test"
    echo -e "${WHITE}📝 Líneas de código:${NC} ~$TOTAL_LINES líneas"
    
    # Estado de compilación
    if [ -d "target/classes" ]; then
        echo -e "${GREEN}✅ Proyecto compilado${NC}"
    else
        echo -e "${YELLOW}⚠️  Proyecto necesita compilación${NC}"
    fi
    
    # Estado de tests
    if [ -f "target/surefire-reports/TEST-*.xml" ]; then
        FAILED_TESTS=$(grep -l "failures=\"[^0]" target/surefire-reports/TEST-*.xml 2>/dev/null | wc -l | xargs)
        if [ "$FAILED_TESTS" -gt 0 ]; then
            echo -e "${YELLOW}⚠️  Hay $FAILED_TESTS tests fallando${NC}"
        else
            echo -e "${GREEN}✅ Todos los tests pasan${NC}"
        fi
    else
        echo -e "${BLUE}ℹ️  Tests no ejecutados${NC}"
    fi
    
    echo
}

# Función para arreglar tests
fix_tests() {
    echo -e "${BLUE}🔧 Guía para Arreglar Tests Fallidos${NC}"
    echo -e "${BLUE}═══════════════════════════════════════${NC}"
    echo
    echo -e "${YELLOW}Tests más comunes que fallan:${NC}"
    echo -e "${WHITE}1. CraneAnimationTest:${NC}"
    echo -e "   - Problema: Estados de animación no cambian correctamente"
    echo -e "   - Solución: Revisar máquina de estados en CraneAnimation.java"
    echo
    echo -e "${WHITE}2. CraneTest:${NC}"
    echo -e "   - Problema: Colisiones y posicionamiento"
    echo -e "   - Solución: Verificar lógica de boundaries en Crane.java"
    echo
    echo -e "${WHITE}3. BlockTest:${NC}"
    echo -e "   - Problema: Detección de colisiones con el suelo"
    echo -e "   - Solución: Implementar AABB collision detection"
    echo
    echo -e "${YELLOW}Comandos útiles para debugging:${NC}"
    echo -e "  mvn test -Dtest=CraneAnimationTest -X    # Debug específico"
    echo -e "  mvn test --fail-fast                     # Parar en primer fallo"
    echo -e "  ./dev.sh test-fast                       # Solo tests rápidos"
    echo
}

# Función para ejecutar con performance monitoring
run_with_performance() {
    echo -e "${PURPLE}🚀 Ejecutando con Monitor de Performance...${NC}"
    mvn clean compile
    java -XX:+PrintGC -XX:+PrintGCDetails -Xmx512m -Xms256m \
         -javaagent:target/classes \
         -cp "target/classes:$(mvn dependency:build-classpath -Dsilent=true -DincludeScope=runtime | grep -v '^\[' | tail -n 1)" \
         com.skillparty.towerblox.Main
}

# Función principal
main() {
    print_header
    
    case "$1" in
        "help"|"-h"|"--help"|"")
            show_help
            ;;
        "clean")
            echo -e "${BLUE}🧹 Limpiando proyecto...${NC}"
            mvn clean
            ;;
        "compile")
            check_prerequisites
            echo -e "${BLUE}🔨 Compilando proyecto...${NC}"
            mvn clean compile
            ;;
        "test")
            check_prerequisites
            echo -e "${BLUE}🧪 Ejecutando todas las pruebas...${NC}"
            mvn test
            ;;
        "test-fast")
            check_prerequisites
            echo -e "${BLUE}⚡ Ejecutando pruebas rápidas...${NC}"
            mvn test -Dtest="*Test" -DfailIfNoTests=false -Dmaven.test.skip.exec=false
            ;;
        "test-fix")
            check_prerequisites
            echo -e "${BLUE}🔧 Ejecutando tests que suelen fallar...${NC}"
            mvn test -Dtest="CraneAnimationTest,CraneTest,BlockTest,GameEngineTest"
            ;;
        "run")
            check_prerequisites
            echo -e "${BLUE}🎮 Ejecutando javaTower...${NC}"
            mvn clean compile exec:java -Dexec.mainClass="com.skillparty.towerblox.Main"
            ;;
        "run-debug")
            check_prerequisites
            echo -e "${PURPLE}🐞 Ejecutando en modo debug...${NC}"
            mvn clean compile exec:java -Dexec.mainClass="com.skillparty.towerblox.Main" -Dexec.args="-Xdebug"
            ;;
        "package")
            check_prerequisites
            echo -e "${BLUE}📦 Creando JAR ejecutable...${NC}"
            mvn clean package -DskipTests
            echo -e "${GREEN}✅ JAR creado: target/tower-bloxx-1.0.0.jar${NC}"
            ;;
        "coverage")
            check_prerequisites
            echo -e "${BLUE}📊 Generando reporte de cobertura...${NC}"
            mvn clean test jacoco:report
            echo -e "${GREEN}✅ Reporte disponible en: target/site/jacoco/index.html${NC}"
            ;;
        "performance")
            check_prerequisites
            run_with_performance
            ;;
        "docs")
            check_prerequisites
            echo -e "${BLUE}📚 Generando documentación JavaDoc...${NC}"
            mvn javadoc:javadoc
            echo -e "${GREEN}✅ Documentación disponible en: target/site/apidocs/index.html${NC}"
            ;;
        "all")
            check_prerequisites
            echo -e "${BLUE}🔄 Compilación completa...${NC}"
            mvn clean compile test package -DskipTests=false
            ;;
        "status")
            check_prerequisites
            show_status
            ;;
        "fix-tests")
            fix_tests
            ;;
        *)
            echo -e "${RED}❌ Comando desconocido: $1${NC}"
            echo -e "${YELLOW}💡 Use '$0 help' para ver los comandos disponibles${NC}"
            exit 1
            ;;
    esac
}

# Ejecutar función principal
main "$@"
