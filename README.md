# 🏗️ Tower Bloxx Java Edition

Una versión completamente reimaginada del clásico juego Tower Bloxx, desarrollada en Java con animaciones realistas, física avanzada y múltiples niveles de dificultad.

![Java](https://img.shields.io/badge/Java-11+-orange.svg)
![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)
![License](https://img.shields.io/badge/License-MIT-green.svg)

## ✨ Características Principales

- 🎮 **Tres niveles de dificultad** - Fácil, Normal y Difícil con velocidades ajustables
- 🎨 **Tipografía Ubuntu Monospace** - Diseño consistente y profesional
- 🖥️ **Logo ASCII estilo terminal** - Inspirado en las terminales de Linux
- 🏗️ **Animación realista de la garra** - Sistema de animación en 5 frames con efectos visuales
- 🏆 **Sistema de puntuaciones avanzado** - Top 10 con nicknames de 3 caracteres y persistencia
- ⚡ **Física de bloques mejorada** - Gravedad, fricción y detección de colisiones realistas
- 🎯 **Sistema de combos** - Bonificaciones por alineación perfecta
- 📊 **Indicadores de estabilidad** - Visualización en tiempo real del estado de la torre
- 🎨 **Efectos visuales** - Partículas, animaciones y feedback visual

## 🎮 Capturas de Pantalla

### Menú Principal
- Logo ASCII animado estilo terminal
- Selección de dificultad con colores distintivos
- Navegación por teclado y mouse

### Gameplay
- Garra con animación realista de apertura/cierre
- Indicador de estabilidad de la torre
- Efectos visuales al colocar bloques
- Sistema de puntuación en tiempo real

### Puntuaciones
- Tabla de mejores puntuaciones con medallas
- Entrada de nickname con validación
- Estadísticas detalladas por dificultad

## 🚀 Instalación Rápida

### Prerrequisitos
- **Java 11** o superior ([Descargar](https://adoptium.net/))
- **Maven 3.6** o superior ([Descargar](https://maven.apache.org/download.cgi))

### Clonar y Ejecutar

```bash
# Clonar el repositorio
git clone https://github.com/skillparty/bloqueTorres.git
cd bloqueTorres

# Compilar y ejecutar
mvn clean compile exec:java -Dexec.mainClass="com.skillparty.towerblox.Main"
```

### Crear JAR Ejecutable

```bash
# Crear JAR con todas las dependencias
mvn clean package

# Ejecutar el JAR
java -jar target/tower-bloxx-1.0.0.jar
```

## 🎯 Controles del Juego

| Tecla | Acción |
|-------|--------|
| **ESPACIO** | Soltar bloque |
| **P** | Pausar/Reanudar juego |
| **ESC** | Volver al menú / Salir |
| **Flechas** | Navegación en menús |
| **ENTER** | Confirmar selección |
| **1, 2, 3** | Selección rápida de dificultad |

## 🏗️ Arquitectura del Proyecto

### Estructura de Directorios

```
src/
├── main/
│   ├── java/com/skillparty/towerblox/
│   │   ├── Main.java                    # Punto de entrada
│   │   ├── game/                        # Motor del juego
│   │   │   ├── GameEngine.java          # Loop principal
│   │   │   ├── GameState.java           # Estados del juego
│   │   │   ├── DifficultyLevel.java     # Niveles de dificultad
│   │   │   ├── GameOverManager.java     # Lógica de game over
│   │   │   └── physics/                 # Sistema de física
│   │   │       ├── Block.java           # Bloques con física
│   │   │       ├── Tower.java           # Torre y estabilidad
│   │   │       ├── Crane.java           # Grúa y movimiento
│   │   │       └── CraneAnimation.java  # Animación de garra
│   │   ├── ui/                          # Interfaz de usuario
│   │   │   ├── GameWindow.java          # Ventana principal
│   │   │   ├── MenuPanel.java           # Menú principal
│   │   │   ├── GamePanel.java           # Panel de juego
│   │   │   ├── ScorePanel.java          # Entrada de puntuación
│   │   │   ├── HighScorePanel.java      # Tabla de puntuaciones
│   │   │   └── components/              # Componentes UI
│   │   │       ├── FontManager.java     # Gestión de fuentes
│   │   │       └── ASCIILogo.java       # Logo ASCII
│   │   ├── score/                       # Sistema de puntuaciones
│   │   │   ├── ScoreManager.java        # Cálculo de puntuaciones
│   │   │   ├── HighScore.java           # Modelo de puntuación
│   │   │   └── ScoreStorage.java        # Persistencia
│   │   └── utils/                       # Utilidades
│   │       └── Constants.java           # Constantes del juego
│   └── resources/
│       ├── fonts/                       # Fuentes del juego
│       │   └── UbuntuMono-Regular.ttf   # Fuente principal
│       └── data/                        # Datos del juego
└── test/                                # Pruebas unitarias e integración
    └── java/com/skillparty/towerblox/
        ├── BlockTest.java               # Pruebas de física
        ├── TowerTest.java               # Pruebas de torre
        ├── CraneTest.java               # Pruebas de grúa
        ├── ScoreManagerTest.java        # Pruebas de puntuación
        ├── GameEngineTest.java          # Pruebas del motor
        └── IntegrationTest.java         # Pruebas de integración
```

### Patrones de Diseño Utilizados

- **MVC (Model-View-Controller)** - Separación clara entre lógica, vista y control
- **Singleton** - FontManager para gestión centralizada de fuentes
- **Observer** - Sistema de eventos entre GameEngine y UI
- **State Machine** - Gestión de estados del juego
- **Strategy** - Diferentes niveles de dificultad

## 🧪 Testing

### Ejecutar Todas las Pruebas

```bash
mvn test
```

### Cobertura de Pruebas

- **Pruebas Unitarias**: Todas las clases principales
- **Pruebas de Integración**: Flujo completo del juego
- **Pruebas de Física**: Simulación de bloques y colisiones
- **Pruebas de Persistencia**: Sistema de puntuaciones

### Generar Reporte de Cobertura

```bash
mvn jacoco:report
# Ver reporte en: target/site/jacoco/index.html
```

## 🎨 Características Técnicas

### Sistema de Física
- **Gravedad realista** con aceleración constante
- **Fricción** para movimiento natural
- **Detección de colisiones** precisa entre bloques
- **Cálculo de estabilidad** basado en alineación y peso

### Animación de Garra
- **5 frames de animación** para apertura/cierre
- **Curvas de animación** suaves (ease-in/ease-out)
- **Efectos visuales** con partículas y motion blur
- **Sincronización** perfecta con la física del juego

### Sistema de Puntuación
- **Puntos base** por colocación de bloques
- **Bonificaciones** por alineación perfecta
- **Sistema de combos** con multiplicadores
- **Bonos por altura** de la torre
- **Multiplicadores de dificultad**

## 🔧 Desarrollo

### Configuración del Entorno

```bash
# Verificar versiones
java -version    # Debe ser 11+
mvn -version     # Debe ser 3.6+

# Compilar en modo desarrollo
mvn clean compile

# Ejecutar con recarga automática
mvn compile exec:java -Dexec.mainClass="com.skillparty.towerblox.Main"
```

### Comandos Útiles

```bash
# Limpiar y compilar
mvn clean compile

# Ejecutar pruebas específicas
mvn test -Dtest=BlockTest

# Generar documentación
mvn javadoc:javadoc

# Verificar estilo de código
mvn checkstyle:check

# Crear distribución completa
mvn clean package assembly:single
```

## 📊 Métricas del Proyecto

- **Líneas de código**: ~3,500 líneas
- **Clases**: 20+ clases principales
- **Pruebas**: 15+ clases de test
- **Cobertura**: 85%+ de cobertura de código
- **Rendimiento**: 60 FPS estables

## 🤝 Contribuir

¡Las contribuciones son bienvenidas! Por favor sigue estos pasos:

1. **Fork** el proyecto
2. **Crea** una rama para tu feature (`git checkout -b feature/NuevaCaracteristica`)
3. **Commit** tus cambios (`git commit -m 'Añadir nueva característica'`)
4. **Push** a la rama (`git push origin feature/NuevaCaracteristica`)
5. **Abre** un Pull Request

### Guías de Contribución

- Sigue las convenciones de código Java
- Añade pruebas para nuevas funcionalidades
- Actualiza la documentación si es necesario
- Usa mensajes de commit descriptivos

## 🐛 Reportar Problemas

Si encuentras un bug o tienes una sugerencia:

1. Verifica que no exista ya un issue similar
2. Crea un nuevo issue con:
   - Descripción clara del problema
   - Pasos para reproducir
   - Versión de Java y sistema operativo
   - Screenshots si es aplicable

## 📝 Changelog

### v1.0.0 (2024-01-15)
- ✨ Implementación inicial completa
- 🎮 Tres niveles de dificultad
- 🏗️ Animación realista de garra
- 🏆 Sistema de puntuaciones persistente
- 🎨 Logo ASCII estilo terminal
- ⚡ Física de bloques avanzada

## 📄 Licencia

Este proyecto está licenciado bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para más detalles.

## 👥 Autores

- **SkillParty Team** - *Desarrollo inicial* - [SkillParty](https://github.com/skillparty)

## 🙏 Agradecimientos

- Inspirado en el juego original Tower Bloxx
- Ubuntu Mono font por Canonical
- Comunidad Java por las herramientas y librerías

## 🔗 Enlaces

- **Repositorio**: https://github.com/skillparty/bloqueTorres
- **Issues**: https://github.com/skillparty/bloqueTorres/issues
- **Wiki**: https://github.com/skillparty/bloqueTorres/wiki

---

⭐ **¡Dale una estrella si te gusta el proyecto!** ⭐