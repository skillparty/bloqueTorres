#!/bin/bash

echo "🔧 DEBUG: Professional Mode Block Generation Issue"
echo "=============================================="
echo ""

# Kill any existing game processes
pkill -f "java.*towerblox" 2>/dev/null

echo "📦 Compilando proyecto..."
mvn clean compile -q

echo "🎮 Iniciando juego en modo debug..."
echo "   Instrucciones:"
echo "   1. Presiona '4' para activar Modo Profesional"
echo "   2. Presiona SPACE para soltar el primer bloque"
echo "   3. Observa los logs de depuración"
echo ""
echo "🚀 Iniciando..."

java -cp target/classes com.skillparty.towerblox.MainIntegrated

echo ""
echo "🔧 Juego terminado. Revisa los logs arriba para debugging."
