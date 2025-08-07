#!/bin/bash
# Script to fix GameEngine.java by removing duplicate content

echo "🔧 Fixing GameEngine.java duplicated content..."

# Find where the duplication starts (look for the second occurrence of setStateListener)
FIRST_SETTER=$(grep -n "public void setStateListener" src/main/java/com/skillparty/towerblox/game/GameEngine.java | head -1 | cut -d: -f1)

if [ -n "$FIRST_SETTER" ]; then
    # Calculate where to cut (3 lines after setStateListener method)
    END_LINE=$((FIRST_SETTER + 3))
    
    echo "📍 Found first setStateListener at line $FIRST_SETTER"
    echo "✂️ Cutting file at line $END_LINE"
    
    # Create clean version
    head -n $END_LINE src/main/java/com/skillparty/towerblox/game/GameEngine.java > temp_engine.java
    echo "}" >> temp_engine.java
    
    # Replace original
    mv temp_engine.java src/main/java/com/skillparty/towerblox/game/GameEngine.java
    
    echo "✅ GameEngine.java fixed successfully"
    echo "📊 File now has $(wc -l < src/main/java/com/skillparty/towerblox/game/GameEngine.java) lines"
    
    # Verify no duplicates
    RENDER_COUNT=$(grep -c "public void render" src/main/java/com/skillparty/towerblox/game/GameEngine.java)
    echo "🔍 Found $RENDER_COUNT render methods (should be 1)"
    
else
    echo "❌ Could not find setStateListener method"
    exit 1
fi
