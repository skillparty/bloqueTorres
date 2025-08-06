#!/bin/bash

echo "╔════════════════════════════════════════════════════════╗"
echo "║        javaBloxx 2.0 - Professional Edition           ║"
echo "║              by joseAlejandro                         ║"
echo "╚════════════════════════════════════════════════════════╝"
echo ""
echo "🎮 Integrated Game Launcher"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Check if build directory exists
if [ ! -d "build" ]; then
    echo "⚠️  Build directory not found. Compiling..."
    echo ""
    
    # Create build directory
    mkdir -p build
    
    # Compile all components
    echo "📦 Compiling game components..."
    
    # Utils and basics
    javac -d build src/main/java/com/skillparty/towerblox/utils/*.java 2>/dev/null
    javac -d build -cp build src/main/java/com/skillparty/towerblox/game/DifficultyLevel.java 2>/dev/null
    
    # UI components
    javac -d build -cp build src/main/java/com/skillparty/towerblox/ui/components/*.java 2>/dev/null
    
    # Refactored core
    javac -d build -cp build src/main/java/com/skillparty/towerblox/core/*.java 2>/dev/null
    javac -d build -cp build src/main/java/com/skillparty/towerblox/physics/*.java 2>/dev/null
    javac -d build -cp build src/main/java/com/skillparty/towerblox/rendering/*.java 2>/dev/null
    
    # Main integration
    javac -d build -cp build src/main/java/com/skillparty/towerblox/MainSimpleIntegration.java
    
    echo "✅ Compilation complete!"
    echo ""
fi

# Display options
echo "Select game mode:"
echo "  1) javaBloxx 2.0 - Refactored (Professional Edition)"
echo "  2) javaBloxx - Legacy (Original Version)"
echo "  3) javaBloxx 2.0 - Test Mode (No UI)"
echo ""
echo -n "Enter choice [1-3] (default: 1): "

# Read user input with timeout
read -t 5 choice

# Default to option 1
if [ -z "$choice" ]; then
    choice=1
    echo "1 (auto-selected)"
fi

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

case $choice in
    1)
        echo "🚀 Starting javaBloxx 2.0 Professional Edition..."
        echo ""
        java -cp build com.skillparty.towerblox.MainSimpleIntegration
        ;;
    2)
        echo "📦 Starting javaBloxx Legacy Version..."
        echo ""
        if [ -f "src/main/java/com/skillparty/towerblox/Main.java" ]; then
            # Compile legacy if needed
            javac -d build -cp build src/main/java/com/skillparty/towerblox/Main.java 2>/dev/null
            java -cp build com.skillparty.towerblox.Main
        else
            echo "❌ Legacy version not found"
        fi
        ;;
    3)
        echo "🔬 Starting Test Mode (Console Only)..."
        echo ""
        java -cp build com.skillparty.towerblox.core.TowerBloxxGame
        ;;
    *)
        echo "❌ Invalid choice. Starting default..."
        java -cp build com.skillparty.towerblox.MainSimpleIntegration
        ;;
esac
