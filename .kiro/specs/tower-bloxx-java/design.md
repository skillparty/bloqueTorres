# Design Document

## Overview

Tower Bloxx Java es un juego de construcción de torres desarrollado en Java usando Swing para la interfaz gráfica. El juego implementa el patrón MVC (Model-View-Controller) para separar la lógica del juego, la presentación visual y el control de entrada. La arquitectura está diseñada para ser modular, permitiendo fácil mantenimiento y extensión de funcionalidades.

## Architecture

El juego sigue una arquitectura basada en componentes con los siguientes módulos principales:

```
src/
├── main/
│   ├── java/
│   │   ├── com/skillparty/towerblox/
│   │   │   ├── Main.java
│   │   │   ├── game/
│   │   │   │   ├── GameEngine.java
│   │   │   │   ├── GameState.java
│   │   │   │   ├── DifficultyLevel.java
│   │   │   │   └── physics/
│   │   │   │       ├── Block.java
│   │   │   │       ├── Tower.java
│   │   │   │       └── Crane.java
│   │   │   ├── ui/
│   │   │   │   ├── GameWindow.java
│   │   │   │   ├── MenuPanel.java
│   │   │   │   ├── GamePanel.java
│   │   │   │   ├── ScorePanel.java
│   │   │   │   └── components/
│   │   │   │       ├── ASCIILogo.java
│   │   │   │       └── FontManager.java
│   │   │   ├── score/
│   │   │   │   ├── ScoreManager.java
│   │   │   │   ├── HighScore.java
│   │   │   │   └── ScoreStorage.java
│   │   │   └── utils/
│   │   │       ├── Constants.java
│   │   │       └── ResourceLoader.java
│   │   └── resources/
│   │       ├── fonts/
│   │       │   └── UbuntuMono-Regular.ttf
│   │       └── data/
│   │           └── highscores.dat
```

## Components and Interfaces

### Game Engine
- **GameEngine**: Clase principal que maneja el loop del juego, actualización de estados y renderizado
- **GameState**: Enum que define los estados del juego (MENU, PLAYING, GAME_OVER, HIGH_SCORES)
- **DifficultyLevel**: Enum que define las dificultades y sus velocidades correspondientes

### Physics System
- **Block**: Representa un bloque individual con propiedades físicas (posición, velocidad, dimensiones)
- **Tower**: Maneja la colección de bloques y verifica estabilidad
- **Crane**: Controla el movimiento de la garra y la animación de apertura/cierre

### User Interface
- **GameWindow**: Ventana principal que contiene todos los paneles
- **MenuPanel**: Panel del menú principal con selección de dificultad
- **GamePanel**: Panel donde se desarrolla el juego
- **ScorePanel**: Panel para mostrar puntuaciones y entrada de nickname

### Score System
- **ScoreManager**: Maneja el cálculo y almacenamiento de puntuaciones
- **HighScore**: Clase de datos para representar una puntuación alta
- **ScoreStorage**: Maneja la persistencia de puntuaciones en archivo

## Data Models

### Block Model
```java
public class Block {
    private double x, y;           // Posición
    private double width, height;  // Dimensiones
    private double velocityX, velocityY; // Velocidad
    private Color color;           // Color del bloque
    private boolean isStable;      // Estado de estabilidad
}
```

### HighScore Model
```java
public class HighScore {
    private String nickname;       // 3 caracteres máximo
    private int score;            // Puntuación
    private DifficultyLevel difficulty; // Dificultad
    private LocalDateTime date;   // Fecha del logro
}
```

### Crane Model
```java
public class Crane {
    private double x;             // Posición horizontal
    private double speed;         // Velocidad de movimiento
    private boolean isOpen;       // Estado de la garra
    private int openingFrame;     // Frame actual de animación
    private Block currentBlock;   // Bloque actual
}
```

## Error Handling

### Font Loading
- Si Ubuntu Monospace no está disponible, usar fuente monospace del sistema
- Mostrar advertencia en consola pero continuar ejecución
- Implementar fallback a Font.MONOSPACED

### File I/O
- Manejo de excepciones para lectura/escritura de puntuaciones
- Crear archivo de puntuaciones si no existe
- Validación de formato de datos al cargar

### Game Physics
- Validación de límites de pantalla para bloques
- Detección de colisiones robusta
- Manejo de casos edge en estabilidad de torre

## Testing Strategy

### Unit Tests
- **BlockTest**: Pruebas de física de bloques individuales
- **TowerTest**: Pruebas de estabilidad y detección de colisiones
- **ScoreManagerTest**: Pruebas de cálculo y almacenamiento de puntuaciones
- **CraneTest**: Pruebas de animación y control de la garra

### Integration Tests
- **GameEngineTest**: Pruebas del loop principal del juego
- **UIIntegrationTest**: Pruebas de interacción entre paneles
- **ScoreStorageTest**: Pruebas de persistencia de datos

### Visual Tests
- Verificación manual de animaciones de garra
- Pruebas de renderizado de logo ASCII
- Validación de tipografía Ubuntu Monospace

## Implementation Details

### Difficulty System
```java
public enum DifficultyLevel {
    EASY(0.7),
    NORMAL(1.0),
    HARD(1.3);
    
    private final double speedMultiplier;
}
```

### ASCII Logo Generation
- Logo generado usando caracteres ASCII art
- Colores inspirados en terminales Linux (verde sobre negro)
- Renderizado usando Graphics2D con fuente monospace

### Crane Animation
- Animación de apertura en 5 frames (20ms cada uno)
- Interpolación suave entre estados cerrado/abierto
- Sincronización con liberación de bloque

### Score Calculation
- Puntos base por bloque colocado: 100
- Bonus por alineación perfecta: 500
- Multiplicador por dificultad: Easy(1.0), Normal(1.5), Hard(2.0)
- Bonus por altura de torre: altura * 50

### Data Persistence
- Archivo binario para puntuaciones (highscores.dat)
- Serialización de objetos HighScore
- Backup automático antes de escribir nuevos datos