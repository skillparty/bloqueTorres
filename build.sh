#!/bin/bash

# Tower Bloxx Java - Build Script
# Este script automatiza las tareas comunes de construcción

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Functions
print_header() {
    echo -e "${BLUE}================================${NC}"
    echo -e "${BLUE} Tower Bloxx Java - Build Script${NC}"
    echo -e "${BLUE}================================${NC}"
    echo
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ $1${NC}"
}

# Check prerequisites
check_prerequisites() {
    print_info "Checking prerequisites..."
    
    if ! command -v java &> /dev/null; then
        print_error "Java is not installed or not in PATH"
        exit 1
    fi
    
    if ! command -v mvn &> /dev/null; then
        print_error "Maven is not installed or not in PATH"
        exit 1
    fi
    
    # Check Java version
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1-2)
    if [[ "$JAVA_VERSION" < "11" ]]; then
        print_error "Java 11 or higher is required. Found: $JAVA_VERSION"
        exit 1
    fi
    
    print_success "Prerequisites check passed"
    print_info "Java version: $(java -version 2>&1 | head -n 1)"
    print_info "Maven version: $(mvn -version | head -n 1)"
    echo
}

# Clean build
clean() {
    print_info "Cleaning project..."
    mvn clean
    print_success "Project cleaned"
    echo
}

# Compile
compile() {
    print_info "Compiling project..."
    mvn compile
    print_success "Project compiled"
    echo
}

# Run tests
test() {
    print_info "Running tests..."
    mvn test
    print_success "Tests completed"
    echo
}

# Generate test coverage report
coverage() {
    print_info "Generating test coverage report..."
    mvn test jacoco:report
    print_success "Coverage report generated at target/site/jacoco/index.html"
    echo
}

# Package JAR
package() {
    print_info "Packaging JAR..."
    mvn package
    print_success "JAR packaged at target/tower-bloxx-1.0.0.jar"
    echo
}

# Run the game
run() {
    print_info "Running Tower Bloxx..."
    mvn exec:java -Dexec.mainClass="com.skillparty.towerblox.Main"
}

# Generate documentation
docs() {
    print_info "Generating JavaDoc documentation..."
    mvn javadoc:javadoc
    print_success "Documentation generated at target/site/apidocs/index.html"
    echo
}

# Full build (clean, compile, test, package)
build() {
    print_info "Starting full build..."
    clean
    compile
    test
    package
    print_success "Full build completed successfully!"
    echo
}

# Release build (full build + docs + coverage)
release() {
    print_info "Starting release build..."
    clean
    compile
    test
    coverage
    docs
    package
    print_success "Release build completed successfully!"
    print_info "Artifacts:"
    print_info "  - JAR: target/tower-bloxx-1.0.0.jar"
    print_info "  - Coverage: target/site/jacoco/index.html"
    print_info "  - Docs: target/site/apidocs/index.html"
    echo
}

# Show help
help() {
    echo "Usage: $0 [command]"
    echo
    echo "Commands:"
    echo "  clean      Clean the project"
    echo "  compile    Compile the project"
    echo "  test       Run tests"
    echo "  coverage   Generate test coverage report"
    echo "  package    Package JAR file"
    echo "  run        Run the game"
    echo "  docs       Generate JavaDoc documentation"
    echo "  build      Full build (clean + compile + test + package)"
    echo "  release    Release build (build + coverage + docs)"
    echo "  help       Show this help message"
    echo
    echo "Examples:"
    echo "  $0 build      # Full build"
    echo "  $0 run        # Run the game"
    echo "  $0 test       # Run tests only"
    echo
}

# Main script
main() {
    print_header
    check_prerequisites
    
    case "${1:-help}" in
        clean)
            clean
            ;;
        compile)
            compile
            ;;
        test)
            test
            ;;
        coverage)
            coverage
            ;;
        package)
            package
            ;;
        run)
            run
            ;;
        docs)
            docs
            ;;
        build)
            build
            ;;
        release)
            release
            ;;
        help|--help|-h)
            help
            ;;
        *)
            print_error "Unknown command: $1"
            echo
            help
            exit 1
            ;;
    esac
}

# Run main function with all arguments
main "$@"