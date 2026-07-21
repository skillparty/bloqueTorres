#!/bin/bash

echo "🚀 Testing javaBloxx 2.0 - Professional Refactor"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Clean build directory
echo "🧹 Cleaning build directory..."
rm -rf build
mkdir -p build

# Compile ImageManager first
echo "📦 Compiling ImageManager..."
javac -d build src/main/java/com/skillparty/towerblox/utils/ImageManager.java

# Compile DifficultyLevel
echo "📦 Compiling DifficultyLevel..."
javac -d build -cp build src/main/java/com/skillparty/towerblox/game/DifficultyLevel.java

# Compile CityBackground
echo "📦 Compiling CityBackground..."
javac -d build -cp build src/main/java/com/skillparty/towerblox/ui/components/CityBackground.java

# Compile core systems
echo "📦 Compiling Core Systems..."
javac -d build -cp build src/main/java/com/skillparty/towerblox/core/GameLoop.java
javac -d build -cp build src/main/java/com/skillparty/towerblox/physics/PhysicsTuning.java
javac -d build -cp build src/main/java/com/skillparty/towerblox/physics/PhysicsEngine.java
javac -d build -cp build src/main/java/com/skillparty/towerblox/physics/CraneSystem.java
javac -d build -cp build src/main/java/com/skillparty/towerblox/physics/BlockSystem.java
javac -d build -cp build src/main/java/com/skillparty/towerblox/rendering/RenderingEngine.java

# Compile main game
echo "📦 Compiling TowerBloxxGame..."
javac -d build -cp build src/main/java/com/skillparty/towerblox/core/TowerBloxxGame.java

# Run the game
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🎮 Starting javaBloxx 2.0..."
echo ""
java -cp build com.skillparty.towerblox.core.TowerBloxxGame
