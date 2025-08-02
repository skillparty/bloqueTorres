@echo off
REM Tower Bloxx Java - Build Script for Windows
REM Este script automatiza las tareas comunes de construcción

setlocal enabledelayedexpansion

REM Colors (limited support in Windows)
set "GREEN=[92m"
set "RED=[91m"
set "YELLOW=[93m"
set "BLUE=[94m"
set "NC=[0m"

:print_header
echo %BLUE%================================%NC%
echo %BLUE% Tower Bloxx Java - Build Script%NC%
echo %BLUE%================================%NC%
echo.
goto :eof

:print_success
echo %GREEN%✓ %~1%NC%
goto :eof

:print_error
echo %RED%✗ %~1%NC%
goto :eof

:print_info
echo %BLUE%ℹ %~1%NC%
goto :eof

:check_prerequisites
call :print_info "Checking prerequisites..."

REM Check Java
java -version >nul 2>&1
if errorlevel 1 (
    call :print_error "Java is not installed or not in PATH"
    exit /b 1
)

REM Check Maven
mvn -version >nul 2>&1
if errorlevel 1 (
    call :print_error "Maven is not installed or not in PATH"
    exit /b 1
)

call :print_success "Prerequisites check passed"
echo.
goto :eof

:clean
call :print_info "Cleaning project..."
mvn clean
if errorlevel 1 exit /b 1
call :print_success "Project cleaned"
echo.
goto :eof

:compile
call :print_info "Compiling project..."
mvn compile
if errorlevel 1 exit /b 1
call :print_success "Project compiled"
echo.
goto :eof

:test
call :print_info "Running tests..."
mvn test
if errorlevel 1 exit /b 1
call :print_success "Tests completed"
echo.
goto :eof

:coverage
call :print_info "Generating test coverage report..."
mvn test jacoco:report
if errorlevel 1 exit /b 1
call :print_success "Coverage report generated at target\site\jacoco\index.html"
echo.
goto :eof

:package
call :print_info "Packaging JAR..."
mvn package
if errorlevel 1 exit /b 1
call :print_success "JAR packaged at target\tower-bloxx-1.0.0.jar"
echo.
goto :eof

:run
call :print_info "Running Tower Bloxx..."
mvn exec:java -Dexec.mainClass="com.skillparty.towerblox.Main"
goto :eof

:docs
call :print_info "Generating JavaDoc documentation..."
mvn javadoc:javadoc
if errorlevel 1 exit /b 1
call :print_success "Documentation generated at target\site\apidocs\index.html"
echo.
goto :eof

:build
call :print_info "Starting full build..."
call :clean
if errorlevel 1 exit /b 1
call :compile
if errorlevel 1 exit /b 1
call :test
if errorlevel 1 exit /b 1
call :package
if errorlevel 1 exit /b 1
call :print_success "Full build completed successfully!"
echo.
goto :eof

:release
call :print_info "Starting release build..."
call :clean
if errorlevel 1 exit /b 1
call :compile
if errorlevel 1 exit /b 1
call :test
if errorlevel 1 exit /b 1
call :coverage
if errorlevel 1 exit /b 1
call :docs
if errorlevel 1 exit /b 1
call :package
if errorlevel 1 exit /b 1
call :print_success "Release build completed successfully!"
call :print_info "Artifacts:"
call :print_info "  - JAR: target\tower-bloxx-1.0.0.jar"
call :print_info "  - Coverage: target\site\jacoco\index.html"
call :print_info "  - Docs: target\site\apidocs\index.html"
echo.
goto :eof

:help
echo Usage: %0 [command]
echo.
echo Commands:
echo   clean      Clean the project
echo   compile    Compile the project
echo   test       Run tests
echo   coverage   Generate test coverage report
echo   package    Package JAR file
echo   run        Run the game
echo   docs       Generate JavaDoc documentation
echo   build      Full build (clean + compile + test + package)
echo   release    Release build (build + coverage + docs)
echo   help       Show this help message
echo.
echo Examples:
echo   %0 build      # Full build
echo   %0 run        # Run the game
echo   %0 test       # Run tests only
echo.
goto :eof

REM Main script
call :print_header
call :check_prerequisites
if errorlevel 1 exit /b 1

if "%1"=="" goto help
if "%1"=="clean" goto clean
if "%1"=="compile" goto compile
if "%1"=="test" goto test
if "%1"=="coverage" goto coverage
if "%1"=="package" goto package
if "%1"=="run" goto run
if "%1"=="docs" goto docs
if "%1"=="build" goto build
if "%1"=="release" goto release
if "%1"=="help" goto help
if "%1"=="--help" goto help
if "%1"=="-h" goto help

call :print_error "Unknown command: %1"
echo.
goto help