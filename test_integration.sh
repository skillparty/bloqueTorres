#!/bin/bash

echo "🚀 Testing javaBloxx 2.0 - Integration"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Clean build directory
echo "🧹 Cleaning build directory..."
rm -rf build
mkdir -p build

# Compile all dependencies first
echo "📦 Compiling dependencies..."
javac -d build src/main/java/com/skillparty/towerblox/utils/*.java 2>/dev/null
javac -d build -cp build src/main/java/com/skillparty/towerblox/game/DifficultyLevel.java

# Compile UI components
echo "📦 Compiling UI components..."
javac -d build -cp build src/main/java/com/skillparty/towerblox/ui/components/FontManager.java
javac -d build -cp build src/main/java/com/skillparty/towerblox/ui/components/ASCIILogo.java
javac -d build -cp build src/main/java/com/skillparty/towerblox/ui/components/CityBackground.java

# Compile refactored systems
echo "📦 Compiling refactored systems..."
javac -d build -cp build src/main/java/com/skillparty/towerblox/core/GameLoop.java
javac -d build -cp build src/main/java/com/skillparty/towerblox/physics/PhysicsEngine.java
javac -d build -cp build src/main/java/com/skillparty/towerblox/physics/CraneSystem.java
javac -d build -cp build src/main/java/com/skillparty/towerblox/physics/BlockSystem.java
javac -d build -cp build src/main/java/com/skillparty/towerblox/rendering/RenderingEngine.java
javac -d build -cp build src/main/java/com/skillparty/towerblox/core/TowerBloxxGame.java

# Compile legacy game components
echo "📦 Compiling legacy components..."
javac -d build -cp build src/main/java/com/skillparty/towerblox/game/*.java 2>/dev/null
javac -d build -cp build src/main/java/com/skillparty/towerblox/ui/*.java 2>/dev/null

# Compile integration components
echo "📦 Compiling integration adapter..."
javac -d build -cp build src/main/java/com/skillparty/towerblox/integration/RefactoredGameAdapter.java

# Compile integrated main
echo "📦 Compiling MainIntegrated..."
javac -d build -cp build src/main/java/com/skillparty/towerblox/MainIntegrated.java

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Check which version to run
if [ "$1" == "--legacy" ]; then
    echo "🎮 Starting javaBloxx (Legacy Version)..."
    java -cp build com.skillparty.towerblox.MainIntegrated --legacy
elif [ "$1" == "--help" ]; then
    java -cp build com.skillparty.towerblox.MainIntegrated --help
else
    echo "🎮 Starting javaBloxx 2.0 (Refactored Version)..."
    echo "   Use './test_integration.sh --legacy' for legacy version"
    echo ""
    java -cp build com.skillparty.towerblox.MainIntegrated --refactored
fi
