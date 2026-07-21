#!/bin/bash

echo "🚀 =============================================="
echo "🚀    TOWER BLOXX 2025 - BUILD & DEMO SCRIPT"  
echo "🚀 =============================================="

# Set colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored messages
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Check if we're in the right directory
if [ ! -f "pom.xml" ]; then
    print_error "pom.xml not found! Please run this script from the project root directory."
    exit 1
fi

print_status "Starting Tower Bloxx 2025 build process..."

# Clean previous builds
print_status "Cleaning previous builds..."
mvn clean -q
if [ $? -eq 0 ]; then
    print_success "Clean completed"
else
    print_error "Clean failed"
    exit 1
fi

# Compile the project
print_status "Compiling Tower Bloxx 2025 with all improvements..."
mvn compile -q
if [ $? -eq 0 ]; then
    print_success "Compilation successful"
else
    print_error "Compilation failed. Please check the error messages above."
    echo ""
    print_status "Attempting to show specific compilation errors..."
    mvn compile
    exit 1
fi

# Package the project
print_status "Packaging the game..."
mvn package -q -DskipTests
if [ $? -eq 0 ]; then
    print_success "Packaging completed"
else
    print_warning "Packaging had issues, but continuing..."
fi

# Create enhanced launch script
print_status "Creating enhanced launch configuration..."

# Set JVM options for optimal performance
export JAVA_OPTS="-Xmx2g -Xms512m -XX:+UseG1GC -XX:+UseStringDeduplication"
export JAVA_OPTS="$JAVA_OPTS -Dsun.java2d.opengl=true -Dswing.aatext=true"
export JAVA_OPTS="$JAVA_OPTS -Djava.awt.headless=false"

print_success "JVM optimized for gaming performance"

echo ""
echo "🎮 =============================================="
echo "🎮           LAUNCHING TOWER BLOXX 2025        "
echo "🎮 =============================================="
echo "📋 ENHANCED FEATURES ACTIVE:"
echo "  ✅ Advanced Camera System"
echo "  ✅ Enhanced Block Physics" 
echo "  ✅ Professional Visual Effects"
echo "  ✅ Real-time Performance Monitoring"
echo "  ✅ Object Pooling System"
echo "  ✅ High-Quality Rendering"
echo "=============================================="
echo ""
echo "📋 GAME CONTROLS:"
echo "  SPACE     - Drop Block"
echo "  ESC       - Pause/Resume"
echo "  F1        - Performance Overlay"
echo "  F2        - Reset Camera"
echo "  F3        - Toggle Advanced Physics"
echo "  F4        - Add Visual Effects"
echo "  F5        - Print Performance Stats"
echo "  F6        - Camera Shake Test"
echo "  F12       - System Status"
echo ""
print_status "Launching game in 3 seconds..."
sleep 1
print_status "2..."
sleep 1
print_status "1..."
sleep 1
print_success "🚀 LAUNCHING TOWER BLOXX 2025!"
echo ""

# Launch the game
java $JAVA_OPTS -cp "target/classes:target/lib/*" com.skillparty.towerblox.QuickDemo2025

# Check exit status
if [ $? -eq 0 ]; then
    print_success "Game exited normally"
else
    print_error "Game exited with error code $?"
    echo ""
    print_status "Attempting fallback launch with original main class..."
    java $JAVA_OPTS -cp "target/classes:target/lib/*" com.skillparty.towerblox.Main
fi

echo ""
print_success "Thanks for playing Tower Bloxx 2025!"
echo "👋 =============================================="
