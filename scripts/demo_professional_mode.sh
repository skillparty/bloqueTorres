#!/bin/bash

# =============================================================================
# TOWER BLOXX - PROFESSIONAL MODE DEMO 2025
# =============================================================================

echo ""
echo "╔═══════════════════════════════════════════════════════════════════╗"
echo "║                    🏆 TOWER BLOXX PROFESSIONAL MODE DEMO           ║"
echo "╚═══════════════════════════════════════════════════════════════════╝"
echo ""

echo "🎮 NUEVO MODO PROFESIONAL IMPLEMENTADO!"
echo ""
echo "┌─ CARACTERÍSTICAS DEL MODO PROFESIONAL ─┐"
echo "│                                         │"
echo "│ 🚀 Velocidad: 1.5x (la más alta)      │"
echo "│ 💰 Puntuación: 3.0x multiplicador      │"
echo "│ 🎨 Interfaz UI Profesional Mejorada    │"
echo "│ 📷 Sistema de Cámara Avanzado          │"
echo "│ 🏗️ Física de Grúa Profesional         │"
echo "│ ✨ Efectos Visuales Mejorados         │"
echo "│ 🎯 Mecánicas Desafiantes               │"
echo "│                                         │"
echo "└─────────────────────────────────────────┘"
echo ""

echo "📋 MODOS DISPONIBLES:"
echo ""
echo "  1️⃣  FÁCIL         - Velocidad: 0.7x | Puntuación: 1.0x"
echo "  2️⃣  NORMAL        - Velocidad: 1.0x | Puntuación: 1.5x"  
echo "  3️⃣  DIFÍCIL       - Velocidad: 1.3x | Puntuación: 2.0x"
echo "  🏆  PROFESIONAL   - Velocidad: 1.5x | Puntuación: 3.0x ⭐ NUEVO"
echo ""

echo "🎯 CARACTERÍSTICAS ESPECIALES DEL MODO PROFESIONAL:"
echo ""
echo "• 🎨 Interfaz con Cards Profesionales Mejoradas"
echo "• 🏗️ Vista Previa de Torre en Tiempo Real con Colores Reales"
echo "• 📊 Indicadores de Estabilidad con Código de Colores"
echo "• ❤️ Corazones Dibujados Custom para las Vidas"
echo "• 🎪 Efectos de Sombra y Gradientes Profesionales"
echo "• 📷 Sistema de Cámara que Sigue la Torre Inteligentemente"
echo "• ⚡ Física de Grúa Avanzada con Movimiento Suave"
echo "• ✨ Efectos de Partículas Mejorados"
echo "• 🏆 Multiplicadores de Puntuación Más Altos"
echo ""

echo "🚀 MEJORAS DE LA INTERFAZ PROFESIONAL:"
echo ""
echo "┌─ TARJETA DE ESTADÍSTICAS ─┐  ┌─ TARJETA DE PROGRESO ─┐  ┌─ TARJETA DE ESTADO ─┐"
echo "│ 💰 Score con formato      │  │ 🏗️ Mini torre preview │  │ ❤️ Vidas visuales   │"
echo "│ 🔥 Combo multiplicador     │  │ 🎨 Colores reales     │  │ 🎮 Controles        │" 
echo "│ 📏 Altura de la torre      │  │ 📊 Barra de progreso  │  │ 🎯 Estado del juego │"
echo "│ ⚖️ Indicador estabilidad   │  │ 🌟 Efectos 3D        │  │ 📈 Información      │"
echo "└──────────────────────────┘  └──────────────────────┘  └──────────────────────┘"
echo ""

echo "🎮 INSTRUCCIONES:"
echo ""
echo "1. Al iniciar el juego, verás el nuevo botón 'PROFESIONAL' en el menú"
echo "2. Usa las teclas de flecha para navegar o presiona '4' para modo profesional"
echo "3. Disfruta de la interfaz profesional mejorada y el gameplay avanzado"
echo "4. Presiona ESPACIO para soltar bloques, P para pausar"
echo ""

echo "📊 RÉCORDS POR MODO:"
echo "Los high scores se guardan separadamente por cada dificultad"
echo "¡Compite en el modo Profesional para obtener las mayores puntuaciones!"
echo ""

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "🚀 ¿Listo para el desafío profesional?"
echo ""
echo "Presiona cualquier tecla para iniciar el juego..."
read -n 1 -s

echo ""
echo "🎮 Iniciando javaBloxx 2.0 Professional Edition con FÍSICA REALISTA..."
echo "🏗️ Activando SISTEMA DE GRÚA PROFESIONAL con:"
echo "   • Péndulo realista con gravedad 9.8"
echo "   • Animación de garra hidráulica"
echo "   • Patrones de movimiento múltiples"
echo "   • Físicas avanzadas y renderizado profesional"
echo ""

# Compilar si es necesario
if [ ! -d "target/classes" ]; then
    echo "🔨 Compilando el proyecto..."
    mvn clean compile -q
fi

# Iniciar el juego con el sistema PROFESIONAL AVANZADO
echo "🚀 Activando TU SISTEMA PROFESIONAL con CraneSystem avanzado..."
java -cp target/classes com.skillparty.towerblox.ProfessionalModeLauncher

echo ""
echo "🎮 ¡Gracias por probar TU SISTEMA PROFESIONAL!"
echo "🏆 Has experimentado física realista de grúa con CraneSystem avanzado!"
echo "🏗️ Incluyendo animaciones de garra hidráulica y patrones de movimiento!"
echo ""
