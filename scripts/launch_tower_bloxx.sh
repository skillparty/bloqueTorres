#!/bin/bash
# Tower Bloxx Game Launcher Script
# Enhanced Edition with Background Fix

echo "=================================================="
echo "    🏗️  TOWER BLOXX - Digital Chocolate Edition"
echo "    📱 Enhanced with Background Image System"
echo "    🎮 Resolution: 1280 x 720 (Fixed & Visible!)"
echo "=================================================="
echo ""
echo "🔧 Preparing game environment..."

# Create build directory if it doesn't exist
mkdir -p build/classes

# Check if we need to compile
if [ ! -d "build/classes/com" ] || [ "src/main/java" -nt "build/classes" ]; then
    echo "📦 Compiling game files..."
    find src/main/java -name "*.java" -not -path "*AdvancedFeaturesManager*" -not -path "*/test/*" | xargs javac -d build/classes -cp "."
    
    if [ $? -eq 0 ]; then
        echo "✅ Compilation successful!"
    else
        echo "❌ Compilation failed!"
        exit 1
    fi
else
    echo "✅ Game already compiled!"
fi

echo ""
echo "🎨 Checking background image..."
if [ -f "public/img/ChatGPT Image Aug 5, 2025 at 10_09_33 PM-2.png" ]; then
    echo "✅ Custom background image found!"
else
    echo "⚠️  Custom background not found, using fallback"
fi

echo ""
echo "🚀 Launching Tower Bloxx..."
echo "   Click PLAY button to start building your tower!"
echo "   Press SPACE to drop blocks during gameplay"
echo ""

cd build/classes
java -Dsun.java2d.opengl=true -Dswing.aatext=true com.skillparty.towerblox.TowerBloxxMain

echo ""
echo "👋 Thanks for playing Tower Bloxx!"
